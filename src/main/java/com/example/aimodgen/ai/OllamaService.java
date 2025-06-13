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
import java.util.Base64;
import java.util.Map;

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
            LOGGER.error("Error generating content with Ollama: " + e.getMessage());            return null;
        }
    }    @Override
    public String generateTexture(String description) {
        // Check if the model supports vision/image generation
        if (supportsImageGeneration()) {
            String imageResult = generateImageWithOllama(description);
            if (imageResult != null) {
                return imageResult;
            }
        }
        
        // Fallback to enhanced text description
        return super.generateTexture(description);
    }

    @Override
    public boolean supportsImageGeneration() {
        // List of models known to support image generation/vision
        String[] imageModels = {
            "llava", "llava:7b", "llava:13b", "llava:34b",
            "bakllava", "moondream", "llava-phi"
        };
        
        for (String imageModel : imageModels) {
            if (model.toLowerCase().contains(imageModel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] generateImage(String description, Map<String, Object> parameters) {
        if (!supportsImageGeneration()) {
            LOGGER.warn("Current model {} does not support image generation", model);
            return null;
        }
        
        try {
            // Use image generation capabilities for supported models
            String imagePrompt = createImageGenerationPrompt(description, parameters);
            String response = generateImageWithOllama(imagePrompt);
            
            if (response != null) {
                // Try to extract image data from response
                String base64Data = extractImageFromResponse(response);
                if (base64Data != null) {
                    return Base64.getDecoder().decode(base64Data);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Image generation failed: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Create image generation prompt
     */
    private String createImageGenerationPrompt(String description, Map<String, Object> parameters) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Generate a 16x16 Minecraft pixel art image for: ").append(description).append("\n\n");
        
        if (parameters != null) {
            if (parameters.containsKey("style")) {
                prompt.append("Style: ").append(parameters.get("style")).append("\n");
            }
            if (parameters.containsKey("width") && parameters.containsKey("height")) {
                prompt.append("Dimensions: ").append(parameters.get("width")).append("x").append(parameters.get("height")).append("\n");
            }
        }
        
        prompt.append("\nRequirements:\n");
        prompt.append("- Minecraft blocky pixel art style\n");
        prompt.append("- 16x16 pixel resolution\n");
        prompt.append("- PNG format\n");
        prompt.append("- Clear, recognizable design\n");
        prompt.append("- Appropriate colors for the item\n\n");
        
        prompt.append("Please generate the image and return it as base64 encoded PNG data.\n");
        
        return prompt.toString();
    }

    /**
     * Extract image data from AI response
     */
    private String extractImageFromResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        
        try {
            // Look for base64 patterns in the response
            String base64Pattern = "[A-Za-z0-9+/]{100,}={0,2}";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(base64Pattern);
            java.util.regex.Matcher matcher = pattern.matcher(response);
            
            if (matcher.find()) {
                return matcher.group();
            }
            
            // Try extracting from data URL format
            if (response.contains("data:image")) {
                int commaIndex = response.indexOf(",");
                if (commaIndex != -1) {
                    String base64Part = response.substring(commaIndex + 1);
                    // Clean up
                    base64Part = base64Part.replaceAll("[^A-Za-z0-9+/=]", "");
                    if (base64Part.length() > 100) {
                        return base64Part;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to extract image from response: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Generate enhanced texture description using vision-capable models
     */
    private String generateImageWithOllama(String description) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl + "/api/generate");
            post.setHeader("Content-Type", "application/json");

            String enhancedPrompt = String.format(
                "Create a detailed JSON description for a 16x16 Minecraft pixel art texture for '%s'. " +
                "Respond with JSON containing 'colorPalette' (array of hex colors), 'pattern' (description), " +
                "and 'pixelMap' (16x16 array of color indices). Focus on Minecraft's blocky aesthetic.",
                description
            );

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.addProperty("prompt", SYSTEM_PROMPT + "\n\n" + enhancedPrompt);
            requestBody.addProperty("stream", false);
            requestBody.addProperty("temperature", 0.7);

            post.setEntity(new StringEntity(gson.toJson(requestBody)));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                String result = responseObj.get("response").getAsString();
                
                LOGGER.info("Generated enhanced texture description with vision model for: {}", description);
                return result;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to generate image description with vision model: " + e.getMessage());
        }
        return null;
    }
}
