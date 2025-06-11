package com.example.aimodgen.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

public class OllamaService extends LLMService {
    private final String baseUrl;
    private final String model;
    private final Gson gson = new Gson();

    public OllamaService(String baseUrl, String model) {
        this.baseUrl = baseUrl;
        this.model = model;
    }

    @Override
    public String generateModContent(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl + "/api/generate");
            post.setHeader("Content-Type", "application/json");

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.addProperty("prompt", SYSTEM_PROMPT + "\n\n" + prompt);
            requestBody.addProperty("stream", false);
            requestBody.addProperty("temperature", 0.7);

            post.setEntity(new StringEntity(gson.toJson(requestBody)));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                return responseObj.get("response").getAsString();
            }
        } catch (Exception e) {
            LOGGER.error("Error generating content with Ollama: " + e.getMessage());
            return null;
        }
    }
}
