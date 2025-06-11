package com.example.aimodgen.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

public class LMStudioService extends LLMService {
    private final String baseUrl;
    private final Gson gson = new Gson();

    public LMStudioService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String generateModContent(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl + "/v1/chat/completions");
            post.setHeader("Content-Type", "application/json");

            JsonObject requestBody = new JsonObject();
            JsonArray messages = new JsonArray();
            
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", SYSTEM_PROMPT);
            messages.add(systemMessage);

            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", prompt);
            messages.add(userMessage);

            requestBody.add("messages", messages);
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("stream", false);

            post.setEntity(new StringEntity(gson.toJson(requestBody)));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                return responseObj.getAsJsonArray("choices").get(0)
                        .getAsJsonObject().getAsJsonObject("message")
                        .get("content").getAsString();
            }
        } catch (Exception e) {
            LOGGER.error("Error generating content with LM Studio: " + e.getMessage());
            return null;
        }
    }
}
