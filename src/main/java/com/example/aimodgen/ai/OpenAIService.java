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

/**
 * Enhanced OpenAI service that supports both text and image generation
 */
public class OpenAIService extends LLMService {
    private final String apiKey;
    private final Gson gson = new Gson();
    private static final String OPENAI_BASE_URL = "https://api.openai.com/v1";

    public OpenAIService(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String generateModContent(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OPENAI_BASE_URL + "/chat/completions");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4");
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 1500);

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
            post.setEntity(new StringEntity(gson.toJson(requestBody)));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                
                if (responseObj.has("choices") && responseObj.getAsJsonArray("choices").size() > 0) {
                    return responseObj.getAsJsonArray("choices").get(0)
                            .getAsJsonObject().getAsJsonObject("message")
                            .get("content").getAsString();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error generating content with OpenAI: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String generateTexture(String description) {
        // Try DALL-E for actual image generation first
        String imageResult = generateImageWithDALLE(description);
        if (imageResult != null) {
            return imageResult;
        }
        
        // Fallback to enhanced text description
        return super.generateTexture(description);
    }

    /**
     * Generate image using DALL-E 3
     */
    private String generateImageWithDALLE(String description) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OPENAI_BASE_URL + "/images/generations");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "dall-e-3");
            requestBody.addProperty("prompt", 
                "Create a 16x16 pixel art texture in Minecraft style for: " + description + 
                ". Should be blocky, pixelated, and clear at small resolution. Use appropriate colors and simple shapes.");
            requestBody.addProperty("n", 1);
            requestBody.addProperty("size", "1024x1024");
            requestBody.addProperty("response_format", "b64_json");

            post.setEntity(new StringEntity(gson.toJson(requestBody)));

            try (CloseableHttpResponse response = client.execute(post)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                    
                    if (responseObj.has("data") && responseObj.getAsJsonArray("data").size() > 0) {
                        JsonObject imageData = responseObj.getAsJsonArray("data").get(0).getAsJsonObject();
                        if (imageData.has("b64_json")) {
                            String base64Data = imageData.get("b64_json").getAsString();
                            LOGGER.info("Successfully generated image with DALL-E for: {}", description);
                            return "{\"base64\": \"" + base64Data + "\"}";
                        }
                    }
                } else {
                    LOGGER.warn("DALL-E request failed with status: {}", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to generate image with DALL-E: {}", e.getMessage());
        }
        return null;
    }
}
