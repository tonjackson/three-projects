package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface ChartService extends IService<Chart> {

    /**
     * 解析AI生成结果
     * @param aiResult AI返回的原始结果
     * @return 长度为2的数组：[genChart, genResult]，解析失败返回null
     */
    String[] parseAiResult(String aiResult);
}
