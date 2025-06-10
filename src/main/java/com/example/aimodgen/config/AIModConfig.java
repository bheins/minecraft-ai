package com.example.aimodgen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AIModConfig {
    private static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static ForgeConfigSpec.ConfigValue<String> OPENAI_API_KEY;
    
    static {
        BUILDER.push("AI Mod Generator Configuration");
        
        OPENAI_API_KEY = BUILDER.comment("Your OpenAI API key")
                .define("openai_api_key", "");
        
        BUILDER.pop();
    }
    
    public static ForgeConfigSpec SPEC = BUILDER.build();
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
