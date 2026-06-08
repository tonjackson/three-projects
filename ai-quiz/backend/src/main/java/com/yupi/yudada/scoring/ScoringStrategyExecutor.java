package com.yupi.yudada.scoring;

import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.exception.BusinessException;
import com.yupi.yudada.model.entity.App;
import com.yupi.yudada.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScoringStrategyExecutor {

    // 策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    // 策略索引：(appType, scoringStrategy) -> ScoringStrategy
    private Map<String, ScoringStrategy> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = scoringStrategyList.stream()
                .filter(strategy -> strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class))
                .collect(Collectors.toMap(
                        strategy -> {
                            ScoringStrategyConfig config = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                            return config.appType() + "_" + config.scoringStrategy();
                        },
                        strategy -> strategy,
                        (a, b) -> a
                ));
    }

    /**
     * 评分
     *
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 通过Map索引快速查找策略
        ScoringStrategy strategy = strategyMap.get(appType + "_" + appScoringStrategy);
        if (strategy != null) {
            return strategy.doScore(choiceList, app);
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
