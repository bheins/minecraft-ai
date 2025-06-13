package com.example.aimodgen.ai;

import com.example.aimodgen.config.AIModConfig;

public class LLMServiceFactory {
    public static LLMService createService() {
        String llmType = AIModConfig.LLM_TYPE.get();
          switch (llmType.toLowerCase()) {
            case "openai":
                String apiKey = AIModConfig.OPENAI_API_KEY.get();
                if (apiKey == null || apiKey.isEmpty()) {
                    throw new IllegalStateException("OpenAI API key is required when using OpenAI service");
                }
                return new OpenAIService(apiKey);
                
            case "lmstudio":
                String lmStudioUrl = AIModConfig.LOCAL_LLM_URL.get();
                return new LMStudioService(lmStudioUrl);
                
            case "ollama":
                String ollamaUrl = AIModConfig.LOCAL_LLM_URL.get();
                String model = AIModConfig.LOCAL_LLM_MODEL.get();
                return new OllamaService(ollamaUrl, model);
                
            default:
                throw new IllegalArgumentException("Unsupported LLM type: " + llmType);
        }
    }
}
