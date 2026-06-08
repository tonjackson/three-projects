package com.yupi.yuoj.judge;

import com.yupi.yuoj.judge.strategy.DefaultJudgeStrategy;
import com.yupi.yuoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.yuoj.judge.strategy.JudgeContext;
import com.yupi.yuoj.judge.strategy.JudgeStrategy;
import com.yupi.yuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.yuoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    @Resource
    private DefaultJudgeStrategy defaultJudgeStrategy;

    @Resource
    private JavaLanguageJudgeStrategy javaLanguageJudgeStrategy;

    /**
     * 语言 -> 判题策略映射
     */
    private Map<String, JudgeStrategy> strategyMap;

    /**
     * 初始化策略映射
     */
    private Map<String, JudgeStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = new HashMap<>();
            strategyMap.put("java", javaLanguageJudgeStrategy);
            // 默认策略
            strategyMap.put("default", defaultJudgeStrategy);
        }
        return strategyMap;
    }

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = getStrategyMap().getOrDefault(language, defaultJudgeStrategy);
        return judgeStrategy.doJudge(judgeContext);
    }

}
