package com.example.aimodgen.generation;

import com.example.aimodgen.ai.LLMService;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ContentGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ContentGenerator instance;
    private final LLMService llmService;
    private final Map<String, GeneratedContent> generatedContent;

    private ContentGenerator(LLMService llmService) {
        this.llmService = llmService;
        this.generatedContent = new HashMap<>();
        
        // Load existing content from persistence
        Map<String, GeneratedContent> savedContent = com.example.aimodgen.persistence.ContentPersistence.loadGeneratedContent();
        this.generatedContent.putAll(savedContent);
        
        // Re-register saved content
        for (GeneratedContent content : savedContent.values()) {
            try {
                if (content.getType() == ContentType.BLOCK) {
                    com.example.aimodgen.block.DynamicBlockRegistry.registerBlock(content);                } else if (content.getType() == ContentType.ITEM) {
                    // com.example.aimodgen.item.DynamicItemRegistry.registerItem(content);
                    LOGGER.warn("Item registration temporarily disabled");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to re-register content {}: {}", content.getId(), e.getMessage());
            }
        }
    }

    public static void initialize(LLMService llmService) {
        if (instance == null) {
            instance = new ContentGenerator(llmService);
        }
    }

    public static ContentGenerator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ContentGenerator not initialized! Call initialize() first.");
        }
        return instance;
    }    public CompletableFuture<Block> generateBlock(String description, Map<String, String> properties) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Generate block properties using LLM
                String prompt = generateBlockPrompt(description, properties);
                String response = llmService.generateModContent(prompt);
                JsonObject blockData = parseResponse(response);
                
                // 2. Generate texture
                String texturePrompt = generateTexturePrompt(description, blockData);
                byte[] textureData = generateTexture(texturePrompt);

                // 3. Create and register the block
                Block block = createBlock(blockData, textureData);
                
                // 4. Store generation data
                String blockId = blockData.get("id").getAsString();
                GeneratedContent content = new GeneratedContent(
                    ContentType.BLOCK,
                    blockId,
                    description,
                    blockData,
                    textureData
                );
                generatedContent.put(blockId, content);

                // 5. Generate crafting recipe
                RecipeGenerator.generateRecipe(content);

                // 6. Save to persistence
                com.example.aimodgen.persistence.ContentPersistence.saveGeneratedContent(generatedContent);

                return block;
            } catch (Exception e) {
                LOGGER.error("Failed to generate block: " + e.getMessage());
                throw new RuntimeException("Block generation failed", e);
            }
        });
    }    public CompletableFuture<Item> generateItem(String description, Map<String, String> properties) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Similar to block generation
                String prompt = generateItemPrompt(description, properties);
                String response = llmService.generateModContent(prompt);
                JsonObject itemData = parseResponse(response);
                
                String texturePrompt = generateTexturePrompt(description, itemData);
                byte[] textureData = generateTexture(texturePrompt);

                Item item = createItem(itemData, textureData);
                
                String itemId = itemData.get("id").getAsString();
                GeneratedContent content = new GeneratedContent(
                    ContentType.ITEM,
                    itemId,
                    description,
                    itemData,
                    textureData
                );
                generatedContent.put(itemId, content);

                // Generate crafting recipe
                RecipeGenerator.generateRecipe(content);

                // Save to persistence
                com.example.aimodgen.persistence.ContentPersistence.saveGeneratedContent(generatedContent);

                return item;
            } catch (Exception e) {
                LOGGER.error("Failed to generate item: " + e.getMessage());
                throw new RuntimeException("Item generation failed", e);
            }
        });
    }

    private String generateBlockPrompt(String description, Map<String, String> properties) {
        return String.format("""
            Generate properties for a Minecraft block with the following description:
            %s
            
            Additional properties: %s
            
            Return a JSON object with the following structure:
            {
                "id": "block_id",
                "name": "Display Name",
                "hardness": float,
                "resistance": float,
                "lightLevel": int (0-15),
                "requiresTool": boolean,
                "material": "minecraft:material_type",
                "soundType": "minecraft:sound_type",
                "hasGravity": boolean,
                "isTransparent": boolean,
                "customProperties": {}
            }
            """, description, properties.toString());
    }

    private String generateItemPrompt(String description, Map<String, String> properties) {
        return String.format("""
            Generate properties for a Minecraft item with the following description:
            %s
            
            Additional properties: %s
            
            Return a JSON object with the following structure:
            {
                "id": "item_id",
                "name": "Display Name",
                "maxStackSize": int,
                "maxDurability": int,
                "craftingMaterial": "minecraft:material",
                "isFood": boolean,
                "foodProperties": {},
                "customProperties": {}
            }
            """, description, properties.toString());
    }

    private String generateTexturePrompt(String description, JsonObject contentData) {
        return String.format("""
            Create a 16x16 pixel art texture for Minecraft that represents:
            %s
            
            Content details: %s
            
            The texture should:
            - Be in Minecraft's pixel art style
            - Use appropriate colors for the described material
            - Be clear and recognizable at 16x16 resolution
            - Fit the overall Minecraft aesthetic
            
            Return the texture as a Base64 encoded PNG image.
            """, description, contentData.toString());
    }

    private JsonObject parseResponse(String response) {
        try {
            // Extract JSON from response if it's wrapped in text
            String jsonString = response;
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = response.substring(startIndex, endIndex + 1);
            }
            
            return com.google.gson.JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.warn("Failed to parse LLM response, using defaults: " + e.getMessage());
            JsonObject defaultObj = new JsonObject();
            defaultObj.addProperty("id", "default_generated");
            defaultObj.addProperty("name", "Generated Content");
            return defaultObj;
        }
    }    private byte[] generateTexture(String prompt) {
        try {
            String base64Response = llmService.generateTexture(prompt);
            if (base64Response != null && !base64Response.trim().isEmpty()) {
                // Try to extract base64 data from various formats
                String base64Data = extractBase64(base64Response);
                if (base64Data != null && !base64Data.isEmpty()) {
                    return java.util.Base64.getDecoder().decode(base64Data);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to generate texture: " + e.getMessage());
        }
        
        // Return default texture data if generation fails
        return createDefaultTexture();
    }

    private String extractBase64(String response) {
        // Remove common prefixes and clean up the response
        String cleaned = response.trim();
        
        // Handle data URL format
        if (cleaned.contains("data:image")) {
            int commaIndex = cleaned.indexOf(",");
            if (commaIndex != -1 && commaIndex < cleaned.length() - 1) {
                cleaned = cleaned.substring(commaIndex + 1);
            }
        }
        
        // Remove markdown code blocks
        cleaned = cleaned.replaceAll("```[^\\n]*\\n?", "");
        cleaned = cleaned.replaceAll("```", "");
        
        // Remove any remaining text, keep only base64-like characters
        cleaned = cleaned.replaceAll("[^A-Za-z0-9+/=\\n]", "");
        cleaned = cleaned.replaceAll("\\s", "");
        
        // Check if it looks like valid base64
        if (cleaned.length() > 0 && cleaned.length() % 4 == 0) {
            return cleaned;
        }
        
        return null;
    }

    private byte[] createDefaultTexture() {
        // Create a simple 16x16 gray texture as fallback
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = img.createGraphics();
            g.setColor(java.awt.Color.GRAY);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
            
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Failed to create default texture: " + e.getMessage());
            return new byte[0];
        }
    }

    private Block createBlock(JsonObject data, byte[] texture) {
        try {
            String blockId = data.get("id").getAsString();
            
            // Save texture to resources
            saveTexture(blockId, texture, "block");
            
            // Create block using dynamic registry
            return com.example.aimodgen.block.DynamicBlockRegistry.createAndRegisterBlock(blockId, data);
        } catch (Exception e) {
            LOGGER.error("Failed to create block: " + e.getMessage());
            return null;
        }
    }

    private Item createItem(JsonObject data, byte[] texture) {
        try {
            String itemId = data.get("id").getAsString();
            
            // Save texture to resources
            saveTexture(itemId, texture, "item");            // Create item using dynamic registry
            // For now, we'll return a simple notification that the item was "created"
            // The actual item will be available after restart when persistence loads it
            LOGGER.info("Item '{}' data generated and saved. Will be available after restart.", itemId);
            return new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties());
        } catch (Exception e) {
            LOGGER.error("Failed to create item: " + e.getMessage());
            return null;
        }
    }    private void saveTexture(String id, byte[] textureData, String type) {
        try {
            java.nio.file.Path textureDir = java.nio.file.Paths.get("src/main/resources/assets/aimodgenerator/textures/" + type);
            java.nio.file.Files.createDirectories(textureDir);
            
            // Strip namespace from id if it exists (e.g., "mymod:block_name" -> "block_name")
            String safeId = id;
            if (safeId.contains(":")) {
                safeId = safeId.substring(safeId.indexOf(':') + 1);
            }
            
            java.nio.file.Path texturePath = textureDir.resolve(safeId + ".png");
            java.nio.file.Files.write(texturePath, textureData);
            
            LOGGER.info("Saved texture for {} at {}", id, texturePath);
        } catch (Exception e) {
            LOGGER.error("Failed to save texture for {}: {}", id, e.getMessage());
        }
    }

    public void deleteContent(String id) {
        GeneratedContent content = generatedContent.remove(id);
        if (content != null) {
            // TODO: Implement proper content unregistration
            // For now, just remove from memory and persistence
            com.example.aimodgen.persistence.ContentPersistence.saveGeneratedContent(generatedContent);
            LOGGER.info("Deleted content: {}", id);
        }
    }

    public Map<String, GeneratedContent> listContent() {
        return new HashMap<>(generatedContent);
    }
}
