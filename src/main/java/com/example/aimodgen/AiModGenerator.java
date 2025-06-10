package com.example.aimodgen;

import com.example.aimodgen.ai.AIModService;
import com.example.aimodgen.config.AIModConfig;
import com.example.aimodgen.init.ModBlocks;
import com.example.aimodgen.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AiModGenerator.MOD_ID)
public class AiModGenerator {
    public static final String MOD_ID = "aimodgenerator";
    private static final Logger LOGGER = LogManager.getLogger();
    private AIModService aiModService;    public AiModGenerator() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
          // Register blocks and items
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        
        // Register config
        AIModConfig.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("AI Mod Generator Initializing...");
        String apiKey = AIModConfig.OPENAI_API_KEY.get();
        
        if (apiKey != null && !apiKey.isEmpty()) {
            aiModService = new AIModService(apiKey);
            LOGGER.info("AI Service initialized successfully");
        } else {
            LOGGER.error("OpenAI API key not configured! Please set it in the config file.");
        }
    }
}
