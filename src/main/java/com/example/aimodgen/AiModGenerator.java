package com.example.aimodgen;

import com.example.aimodgen.ai.LLMService;
import com.example.aimodgen.ai.LLMServiceFactory;
import com.example.aimodgen.block.DynamicBlockRegistry;
import com.example.aimodgen.commands.AIModCommands;
import com.example.aimodgen.config.AIModConfig;
import com.example.aimodgen.generation.ContentGenerator;
import com.example.aimodgen.generation.ContentRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AiModGenerator.MOD_ID)
public class AiModGenerator {
    public static final String MOD_ID = "aimodgenerator";
    private static final Logger LOGGER = LogManager.getLogger();
    private LLMService llmService;
    private static AiModGenerator instance;

    public AiModGenerator() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register dynamic blocks
        DynamicBlockRegistry.BLOCKS.register(modEventBus);
        
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        
        // Register config and initialize content registry
        AIModConfig.register();
        ContentRegistry.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("AI Mod Generator Initializing...");
        try {
            llmService = LLMServiceFactory.createService();
            ContentGenerator.initialize(llmService);
            LOGGER.info("AI Service initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize AI Service: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        AIModCommands.register(event.getDispatcher());
        LOGGER.info("AI Mod Generator commands registered");
    }

    public LLMService getLlmService() {
        return llmService;
    }

    public static AiModGenerator getInstance() {
        return instance;
    }
}