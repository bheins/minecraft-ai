package com.example.aimodgen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AIModConfig {
    private static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static ForgeConfigSpec.ConfigValue<String> OPENAI_API_KEY;
    public static ForgeConfigSpec.ConfigValue<String> LLM_TYPE;
    public static ForgeConfigSpec.ConfigValue<String> LOCAL_LLM_URL;
    public static ForgeConfigSpec.ConfigValue<String> LOCAL_LLM_MODEL;
    
    static {
        BUILDER.push("AI Mod Generator Configuration");
        
        LLM_TYPE = BUILDER.comment("Type of LLM to use (openai, lmstudio, or ollama)")
                .define("llm_type", "openai");
        
        OPENAI_API_KEY = BUILDER.comment("Your OpenAI API key (only needed if llm_type is openai)")
                .define("openai_api_key", "");
                
        LOCAL_LLM_URL = BUILDER.comment("URL for local LLM service (e.g., http://localhost:1234 for LM Studio or http://localhost:11434 for Ollama)")
                .define("local_llm_url", "http://localhost:1234");
                
        LOCAL_LLM_MODEL = BUILDER.comment("Model name for local LLM (e.g., mistral for Ollama)")
                .define("local_llm_model", "mistral");
        
        BUILDER.pop();
    }
    
    public static ForgeConfigSpec SPEC = BUILDER.build();
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
