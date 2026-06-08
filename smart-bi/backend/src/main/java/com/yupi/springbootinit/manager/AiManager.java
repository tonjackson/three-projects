package com.yupi.springbootinit.manager;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 用于对接 AI 平台（Kimi/Moonshot OpenAI兼容接口）
 */
@Service
@Slf4j
@ConfigurationProperties(prefix = "ai")
@Data
public class AiManager {

    private String apiKey;
    private String baseUrl = "https://api.moonshot.cn/v1";
    private String model = "moonshot-v1-8k";

    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * AI 对话
     *
     * @param modelId 模型ID（保留兼容，实际使用配置的model）
     * @param message 用户消息
     * @return AI回复内容
     */
    public String doChat(long modelId, String message) {
        JSONArray messages = new JSONArray();
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", message);
        messages.add(userMsg);

        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);
        requestBody.set("messages", messages);
        requestBody.set("stream", false);
        requestBody.set("temperature", 0.7f);

        String url = baseUrl + "/chat/completions";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "unknown";
                    log.error("AI调用失败, status={}, body={}", response.code(), errorBody);
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务异常: " + response.code());
                }
                String responseBody = response.body().string();
                JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices == null || choices.isEmpty()) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回结果为空");
                }
                return choices.getJSONObject(0).getByPath("message.content", String.class);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI调用异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务异常，请稍后重试");
        }
    }
}
