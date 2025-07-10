package com.chenyuxin.xinaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/10 17:02
 */
public class AliYunHttpInvoke {
    public static void main(String[] args) {
        // 从环境变量获取 API Key，或者直接替换为你的实际 API Key
        String dashScopeApiKey = TestApiKey.ALI_YUN_BAI_LIAN_APIKEY;
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        // 构建请求体 (JSON)
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        messages.add(systemMessage);
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        messages.add(userMessage);
        input.set("messages", messages);
        requestBody.set("input", input);
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);
//        // 打印请求体（可选，用于调试）
//        System.out.println("请求体: " + requestBody.toStringPretty());
        // 发送 HTTP POST 请求
        try {
            String result = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + dashScopeApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .execute()
                    .body();
            System.out.println("\nAPI 响应:\n" + result);
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
