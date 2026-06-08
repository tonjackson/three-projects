package com.yupi.yudada.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.exception.BusinessException;
import com.yupi.yudada.manager.AiManager;
import com.yupi.yudada.model.dto.question.QuestionAnswerDTO;
import com.yupi.yudada.model.dto.question.QuestionContentDTO;
import com.yupi.yudada.model.entity.App;
import com.yupi.yudada.model.entity.Question;
import com.yupi.yudada.model.entity.User;
import com.yupi.yudada.model.entity.UserAnswer;
import com.yupi.yudada.model.vo.QuestionVO;
import com.yupi.yudada.service.QuestionService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI 测评类应用评分策略
 *
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Autowired(required = false)
    private RedissonClient redissonClient;

    // 分布式锁的 key
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * AI 评分结果缓存（使用Redis替代Caffeine本地缓存，保证多节点一致性）
     */
    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    private static final String AI_ANSWER_CACHE_PREFIX = "ai:answer:cache:";
    private static final long AI_ANSWER_CACHE_TTL = 5;

    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        
        // 如果Redis可用，尝试从缓存获取
        if (stringRedisTemplate != null) {
            String answerJson = stringRedisTemplate.opsForValue().get(AI_ANSWER_CACHE_PREFIX + cacheKey);
            // 如果有缓存，直接返回
            if (StrUtil.isNotBlank(answerJson)) {
                // 构造返回值，填充答案对象的属性
                UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
                userAnswer.setAppId(appId);
                userAnswer.setAppType(app.getAppType());
                userAnswer.setScoringStrategy(app.getScoringStrategy());
                userAnswer.setChoices(jsonStr);
                return userAnswer;
            }
        }

        // 如果Redis可用，使用分布式锁；否则直接执行
        if (redissonClient != null) {
            RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + appId + ":" + cacheKey);
            try {
                boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
                if (!res) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
                }
                return doAiScoring(appId, app, jsonStr, choices, cacheKey);
            } finally {
                if (lock != null && lock.isLocked()) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        } else {
            return doAiScoring(appId, app, jsonStr, choices, cacheKey);
        }

    }

    /**
     * 执行AI评分核心逻辑
     */
    private UserAnswer doAiScoring(Long appId, App app, String jsonStr, List<String> choices, String cacheKey) throws Exception {
        // 1. 根据 id 查询到题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 2. 调用 AI 获取结果
        String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
        String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\{.*}").matcher(result);
        String json = matcher.find() ? matcher.group() : "";

        // 缓存结果（如果Redis可用）
        if (stringRedisTemplate != null) {
            stringRedisTemplate.opsForValue().set(AI_ANSWER_CACHE_PREFIX + cacheKey, json, AI_ANSWER_CACHE_TTL, TimeUnit.MINUTES);
        }

        // 3. 构造返回值
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(jsonStr);
        return userAnswer;
    }

    /**
     * AI 评分用户消息封装
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }


    /**
     * 构建缓存 key
     *
     * @param appId
     * @param choices
     * @return
     */
    private String buildCacheKey(Long appId, String choices) {
        return DigestUtil.md5Hex(appId + ":" + choices);
    }

}

