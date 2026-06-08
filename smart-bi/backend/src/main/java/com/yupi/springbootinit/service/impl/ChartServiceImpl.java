package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    private static final String AI_RESULT_SEPARATOR = "【【【【【";

    @Override
    public String[] parseAiResult(String aiResult) {
        if (aiResult == null || aiResult.isEmpty()) {
            return null;
        }
        String[] splits = aiResult.split(AI_RESULT_SEPARATOR);
        if (splits.length < 3) {
            log.warn("AI生成结果格式异常，分隔符分割后不足3段: {}", aiResult.length() > 200 ? aiResult.substring(0, 200) + "..." : aiResult);
            return null;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 校验genChart是否为合法的Echarts JSON
        if (!genChart.startsWith("{") && !genChart.startsWith("[")) {
            log.warn("AI生成的图表代码不是合法JSON格式");
            return null;
        }
        return new String[]{genChart, genResult};
    }
}




