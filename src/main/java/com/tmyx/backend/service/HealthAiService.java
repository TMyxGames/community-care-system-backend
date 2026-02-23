package com.tmyx.backend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class HealthAiService {
    @Value("${deepseek.api.key}") // 在 application.yml 中配置 key
    private String apiKey;

    private final String API_URL = "https://api.deepseek.com/chat/completions";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String getHealthAdvice(String prompt) throws IOException {
        // 构建请求 JSON (使用 Fastjson 语法)
        JSONObject root = new JSONObject();
        root.put("model", "deepseek-chat");
        root.put("stream", false);

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位专业的健康管理专家。");
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        root.put("messages", messages);

        // 构建 RequestBody (注意 OkHttp 4.x 语法)
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(root.toJSONString(), mediaType);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 发送请求并解析结果
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("API请求失败: " + response.code() + " " + response.message());
            }

            String responseString = response.body().string();
            JSONObject resJson = JSON.parseObject(responseString);

            return resJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
