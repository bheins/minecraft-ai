package com.example.aimodgen.ai;

import com.google.gson.JsonObject;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

public class AIModService extends LLMService {
    private final OpenAiService openAiService;

    public AIModService(String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    @Override
    public String generateModContent(String prompt) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", SYSTEM_PROMPT));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-3.5-turbo")
                    .temperature(0.7)
                    .build();

            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            LOGGER.info("Generated mod content for prompt: " + prompt);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error generating mod content: " + e.getMessage());
            return null;
        }
    }

    public JsonObject generateItemProperties(String itemName, String itemDescription) {
        String prompt = String.format("Generate balanced Minecraft item properties for an item named '%s' with description '%s'. " +
                "Include properties like damage, durability, and special effects.", itemName, itemDescription);
        
        String response = generateModContent(prompt);
        // TODO: Parse the response and convert it to proper item properties
        JsonObject properties = new JsonObject();
        // Add basic properties parsing here
        return properties;
    }
}
