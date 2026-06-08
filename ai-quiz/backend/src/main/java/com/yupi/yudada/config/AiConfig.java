package com.yupi.yudada.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.moonshot.cn/v1";

    /**
     * 模型名称
     */
    private String model = "moonshot-v1-8k";
}
