package com.example.aimodgen.block;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.generation.GeneratedContent;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DynamicBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AiModGenerator.MOD_ID);
    private static final Map<String, RegistryObject<Block>> registeredBlocks = new HashMap<>();

    public static RegistryObject<Block> registerBlock(GeneratedContent content) {
        if (registeredBlocks.containsKey(content.getId())) {
            return registeredBlocks.get(content.getId());
        }

        JsonObject properties = content.getProperties();
        BlockBehaviour.Properties blockProps = createBlockProperties(properties);
        
        RegistryObject<Block> blockReg = BLOCKS.register(content.getId(), 
            () -> new AIGeneratedBlock(blockProps, content));
            
        registeredBlocks.put(content.getId(), blockReg);
        return blockReg;
    }

    private static BlockBehaviour.Properties createBlockProperties(JsonObject properties) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.STONE);
        
        if (properties.has("hardness")) {
            props.strength(properties.get("hardness").getAsFloat());
        }
        if (properties.has("light_level")) {
            props.lightLevel((state) -> properties.get("light_level").getAsInt());
        }        if (properties.has("sound_type")) {
            String soundName = properties.get("sound_type").getAsString().toUpperCase();
            switch (soundName) {
                case "STONE" -> props.sound(SoundType.STONE);
                case "WOOD" -> props.sound(SoundType.WOOD);
                case "METAL" -> props.sound(SoundType.METAL);
                case "GLASS" -> props.sound(SoundType.GLASS);
                case "SAND" -> props.sound(SoundType.SAND);
                case "GRAVEL" -> props.sound(SoundType.GRAVEL);
                default -> props.sound(SoundType.STONE);
            }
        }
        
        return props;
    }

    public static Map<String, RegistryObject<Block>> getRegisteredBlocks() {
        return registeredBlocks;
    }    public static Block createAndRegisterBlock(String blockId, JsonObject properties) {
        try {
            // Strip namespace if needed for registration
            String registryName = blockId;
            if (registryName.contains(":")) {
                registryName = registryName.substring(registryName.indexOf(':') + 1);
            }
            
            // Create GeneratedContent object
            GeneratedContent content = new GeneratedContent(
                com.example.aimodgen.generation.ContentType.BLOCK,
                blockId,
                properties.has("name") ? properties.get("name").getAsString() : blockId,
                properties,
                new byte[0] // Texture data handled separately
            );
            
            // Check if we can register at this time by attempting registration
            try {
                // Try to register the block
                RegistryObject<Block> blockReg = registerBlock(content);
                return blockReg.get();
            } catch (Exception regEx) {
                if (regEx.getMessage() != null && regEx.getMessage().contains("after RegisterEvent has been fired")) {
                    // Registration is closed, save for next startup
                    org.apache.logging.log4j.LogManager.getLogger().info(
                        "Registration is closed. Block {} will be available after restart.", blockId);
                    
                    // Store in persistence for next startup
                    com.example.aimodgen.persistence.ContentPersistence.addPendingContent(content);
                    
                    // Return a temporary block that won't be registered
                    BlockBehaviour.Properties props = createBlockProperties(properties);
                    return new AIGeneratedBlock(props, content);
                } else {
                    // Some other registration error
                    throw regEx;
                }
            }
        } catch (Exception e) {
            org.apache.logging.log4j.LogManager.getLogger().error(
                "Failed to create block {}: {}", blockId, e.getMessage());
            throw e;
        }
    }
}
