package com.yupi.yudada.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.config.AiConfig;
import com.yupi.yudada.exception.BusinessException;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通用 AI 调用能力（Kimi/Moonshot OpenAI兼容接口）
 */
@Component
@Slf4j
public class AiManager {

    @Resource
    private AiConfig aiConfig;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // 稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * 同步请求（答案不稳定）
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, UNSTABLE_TEMPERATURE);
    }

    /**
     * 同步请求（答案较稳定）
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 同步请求
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, temperature);
    }

    /**
     * 通用请求（简化消息传递）
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        return doRequest(systemMessage, userMessage, temperature);
    }

    /**
     * 通用同步请求
     */
    private String doRequest(String systemMessage, String userMessage, Float temperature) {
        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", systemMessage);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);
        messages.add(userMsg);

        return doRequest(messages, temperature);
    }

    /**
     * 通用同步请求
     */
    private String doRequest(JSONArray messages, Float temperature) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", aiConfig.getModel());
        requestBody.set("messages", messages);
        requestBody.set("stream", false);
        requestBody.set("temperature", temperature != null ? temperature : STABLE_TEMPERATURE);

        String url = aiConfig.getBaseUrl() + "/chat/completions";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + aiConfig.getApiKey())
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

    /**
     * 流式请求返回类型（兼容原有SSE逻辑）
     */
    public static class StreamChunk {
        private final String content;

        public StreamChunk(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    /**
     * 通用流式请求（简化消息传递）
     * 返回Flowable以兼容原有SSE逻辑
     */
    public Flowable<StreamChunk> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", systemMessage);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);
        messages.add(userMsg);

        return doStreamRequest(messages, temperature);
    }

    /**
     * 通用流式请求
     */
    public Flowable<StreamChunk> doStreamRequest(JSONArray messages, Float temperature) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", aiConfig.getModel());
        requestBody.set("messages", messages);
        requestBody.set("stream", true);
        requestBody.set("temperature", temperature != null ? temperature : STABLE_TEMPERATURE);

        String url = aiConfig.getBaseUrl() + "/chat/completions";

        return Flowable.create(emitter -> {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + aiConfig.getApiKey())
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                        .build();

                Response response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "unknown";
                    emitter.onError(new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务异常: " + response.code()));
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                        try {
                            JSONObject json = JSONUtil.parseObj(data);
                            JSONArray choices = json.getJSONArray("choices");
                            if (choices != null && !choices.isEmpty()) {
                                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                                if (delta != null && delta.containsKey("content")) {
                                    String content = delta.getStr("content");
                                    if (StrUtil.isNotBlank(content)) {
                                        emitter.onNext(new StreamChunk(content));
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // 跳过无法解析的行
                        }
                    }
                }
                reader.close();
                response.close();
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, io.reactivex.BackpressureStrategy.BUFFER);
    }
}
