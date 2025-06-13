package com.example.aimodgen.generation;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.ai.LLMService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Advanced AI-powered texture generation service with multiple enhancement strategies
 */
public class AdvancedTextureGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    
    // Cache for generated textures to avoid regeneration
    private static final Map<String, CachedTexture> textureCache = new HashMap<>();
    
    /**
     * Generate enhanced texture with multiple strategies
     */
    public static CompletableFuture<ResourceLocation> generateEnhancedTexture(
            String name, String description, String type, TextureGenerationOptions options) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("Starting enhanced texture generation for: {}", name);
                
                // Check cache first
                String cacheKey = createCacheKey(name, description, options);
                CachedTexture cached = textureCache.get(cacheKey);
                if (cached != null && cached.isValid()) {
                    LOGGER.info("Using cached texture for: {}", name);
                    return cached.getResourceLocation();
                }
                
                // Try multiple generation strategies
                ResourceLocation result = tryMultipleStrategies(name, description, type, options);
                
                if (result != null) {
                    // Cache the successful result
                    textureCache.put(cacheKey, new CachedTexture(result, System.currentTimeMillis()));
                    LOGGER.info("Successfully generated enhanced texture for: {}", name);
                    return result;
                }
                
                // Fallback to advanced generated texture
                LOGGER.warn("All strategies failed, using advanced fallback for: {}", name);
                return TextureGenerator.generateAdvancedTexture(name, description, type);
                
            } catch (Exception e) {
                LOGGER.error("Enhanced texture generation failed for {}: {}", name, e.getMessage());
                return TextureGenerator.generateAdvancedTexture(name, description, type);
            }
        }, executor);
    }
    
    /**
     * Try multiple texture generation strategies
     */
    private static ResourceLocation tryMultipleStrategies(String name, String description, String type, TextureGenerationOptions options) {
        LLMService llmService = AiModGenerator.getInstance().getLlmService();
        
        // Strategy 1: Direct image generation (if supported)
        if (llmService.supportsImageGeneration() && options.useImageGeneration) {
            ResourceLocation imageResult = tryDirectImageGeneration(name, description, type, llmService, options);
            if (imageResult != null) {
                LOGGER.info("Strategy 1 (Direct Image) succeeded for: {}", name);
                return imageResult;
            }
        }
        
        // Strategy 2: Enhanced multi-stage generation
        if (options.useEnhancedPrompts) {
            ResourceLocation enhancedResult = tryEnhancedGeneration(name, description, type, llmService, options);
            if (enhancedResult != null) {
                LOGGER.info("Strategy 2 (Enhanced Prompts) succeeded for: {}", name);
                return enhancedResult;
            }
        }
        
        // Strategy 3: Style-specific generation
        if (options.style != null && !options.style.isEmpty()) {
            ResourceLocation styledResult = tryStyledGeneration(name, description, type, llmService, options);
            if (styledResult != null) {
                LOGGER.info("Strategy 3 (Styled) succeeded for: {}", name);
                return styledResult;
            }
        }
        
        // Strategy 4: Variation-based generation
        ResourceLocation variationResult = tryVariationGeneration(name, description, type, llmService, options);
        if (variationResult != null) {
            LOGGER.info("Strategy 4 (Variation) succeeded for: {}", name);
            return variationResult;
        }
        
        return null;
    }
    
    /**
     * Try direct image generation for services that support it
     */
    private static ResourceLocation tryDirectImageGeneration(String name, String description, String type, 
                                                           LLMService llmService, TextureGenerationOptions options) {
        try {
            Map<String, Object> imageParams = new HashMap<>();
            imageParams.put("width", 16);
            imageParams.put("height", 16);
            imageParams.put("style", "minecraft_pixel_art");
            imageParams.put("description", description);
            
            byte[] imageData = llmService.generateImage(description, imageParams);
            if (imageData != null && imageData.length > 0) {
                return saveGeneratedImage(name, imageData, type);
            }
        } catch (Exception e) {
            LOGGER.warn("Direct image generation failed: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Try enhanced multi-stage generation
     */
    private static ResourceLocation tryEnhancedGeneration(String name, String description, String type, 
                                                        LLMService llmService, TextureGenerationOptions options) {
        try {
            Map<String, Object> enhanceOptions = new HashMap<>();
            enhanceOptions.put("enhance", true);
            enhanceOptions.put("detailLevel", options.detailLevel);
            enhanceOptions.put("quality", "high");
            
            String enhanced = llmService.generateEnhancedTexture(description, type, enhanceOptions);
            if (enhanced != null && !enhanced.trim().isEmpty()) {
                return processAIResponse(name, description, enhanced, type);
            }
        } catch (Exception e) {
            LOGGER.warn("Enhanced generation failed: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Try style-specific generation
     */
    private static ResourceLocation tryStyledGeneration(String name, String description, String type, 
                                                      LLMService llmService, TextureGenerationOptions options) {
        try {
            String styled = llmService.generateStyledTexture(description, options.style, options.styleParameters);
            if (styled != null && !styled.trim().isEmpty()) {
                return processAIResponse(name, description, styled, type);
            }
        } catch (Exception e) {
            LOGGER.warn("Styled generation failed: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Try variation-based generation
     */
    private static ResourceLocation tryVariationGeneration(String name, String description, String type, 
                                                         LLMService llmService, TextureGenerationOptions options) {
        try {
            List<String> variations = llmService.generateTextureVariations(description, type, 3);
            if (!variations.isEmpty()) {
                // Try the first variation that works
                for (String variation : variations) {
                    ResourceLocation result = processAIResponse(name, description, variation, type);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Variation generation failed: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Process AI response and create texture
     */
    private static ResourceLocation processAIResponse(String name, String description, String aiResponse, String type) {
        try {
            // Try to parse as JSON first
            JsonObject textureData = parseTextureResponse(aiResponse);
            if (textureData != null) {
                return generateFromTextureData(name, description, textureData, type);
            }
            
            // Try to extract base64 image
            String base64Image = extractBase64FromResponse(aiResponse);
            if (base64Image != null) {
                byte[] imageData = Base64.getDecoder().decode(base64Image);
                return saveGeneratedImage(name, imageData, type);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to process AI response: {}", e.getMessage());
        }
        return null;
    }
      /**
     * Generate texture from parsed texture data
     */
    private static ResourceLocation generateFromTextureData(String name, String description, JsonObject textureData, String type) {
        try {
            // Extract color palette
            List<Color> colorPalette = extractColorPalette(textureData);
            if (colorPalette.isEmpty()) {
                colorPalette = generateSmartColors(description);
            }
            
            // Extract pattern information
            String pattern = textureData.has("pattern") ? textureData.get("pattern").getAsString() : "generic";
            String textureType = textureData.has("textureType") ? textureData.get("textureType").getAsString() : "generic";
            
            // Create enhanced texture
            BufferedImage image = createEnhancedTexture(name, description, colorPalette, pattern, textureType);
            return saveTextureImage(name, image, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to generate from texture data: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Create enhanced texture with advanced patterns
     */
    private static BufferedImage createEnhancedTexture(String name, String description, List<Color> colorPalette, 
                                                     String pattern, String textureType) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable better rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        Random random = new Random(name.hashCode());
        
        // Apply base color
        Color baseColor = colorPalette.get(0);
        g2d.setColor(baseColor);
        g2d.fillRect(0, 0, 16, 16);
        
        // Apply pattern based on type
        switch (textureType.toLowerCase()) {
            case "sword":
            case "blade":
                createAdvancedSwordPattern(g2d, colorPalette, random, pattern);
                break;
            case "wand":
            case "staff":
                createAdvancedWandPattern(g2d, colorPalette, random, pattern);
                break;
            case "crystal":
            case "gem":
                createAdvancedCrystalPattern(g2d, colorPalette, random, pattern);
                break;
            case "fire":
            case "flame":
                createAdvancedFirePattern(g2d, colorPalette, random, pattern);
                break;
            case "ice":
            case "frost":
                createAdvancedIcePattern(g2d, colorPalette, random, pattern);
                break;
            default:
                createAdvancedGenericPattern(g2d, colorPalette, random, description, pattern);
                break;
        }
        
        // Add enhancement effects
        addEnhancementEffects(g2d, colorPalette, textureType, random);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Create advanced sword pattern with more detail
     */
    private static void createAdvancedSwordPattern(Graphics2D g2d, List<Color> colors, Random random, String pattern) {
        // Main blade
        Color bladeColor = colors.size() > 1 ? colors.get(1) : colors.get(0).brighter();
        g2d.setColor(bladeColor);
        g2d.fillRect(6, 2, 4, 9);
        
        // Blade edges
        Color edgeColor = bladeColor.darker();
        g2d.setColor(edgeColor);
        g2d.drawLine(6, 2, 6, 10);
        g2d.drawLine(9, 2, 9, 10);
        
        // Crossguard
        Color guardColor = colors.size() > 2 ? colors.get(2) : colors.get(0);
        g2d.setColor(guardColor);
        g2d.fillRect(4, 11, 8, 2);
        
        // Handle
        Color handleColor = guardColor.darker();
        g2d.setColor(handleColor);
        g2d.fillRect(7, 13, 2, 3);
        
        // Shine line
        g2d.setColor(Color.WHITE);
        g2d.drawLine(7, 3, 7, 9);
        
        // Pattern details based on description
        if (pattern.contains("ornate") || pattern.contains("decorated")) {
            addOrnateDetails(g2d, colors, random);
        }
    }
    
    /**
     * Create advanced wand pattern
     */
    private static void createAdvancedWandPattern(Graphics2D g2d, List<Color> colors, Random random, String pattern) {
        // Handle
        Color handleColor = colors.size() > 1 ? colors.get(1) : new Color(139, 69, 19);
        g2d.setColor(handleColor);
        g2d.fillRect(7, 8, 2, 6);
        
        // Wand tip
        Color tipColor = colors.size() > 2 ? colors.get(2) : Color.CYAN;
        g2d.setColor(tipColor);
        g2d.fillRect(6, 5, 4, 4);
        
        // Magical orb
        Color orbColor = tipColor.brighter();
        g2d.setColor(orbColor);
        g2d.fillRect(7, 6, 2, 2);
        
        // Sparkles
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(16);
            int y = random.nextInt(8);
            if (random.nextFloat() < 0.6f) {
                g2d.fillRect(x, y, 1, 1);
            }
        }
        
        // Magical aura
        if (pattern.contains("glowing") || pattern.contains("magical")) {
            addMagicalAura(g2d, colors, random);
        }
    }
    
    /**
     * Add ornate details to weapons
     */
    private static void addOrnateDetails(Graphics2D g2d, List<Color> colors, Random random) {
        Color detailColor = colors.get(colors.size() - 1);
        g2d.setColor(detailColor);
        
        // Add small decorative elements
        g2d.fillRect(8, 4, 1, 1);
        g2d.fillRect(8, 6, 1, 1);
        g2d.fillRect(8, 8, 1, 1);
    }
    
    /**
     * Add magical aura effects
     */
    private static void addMagicalAura(Graphics2D g2d, List<Color> colors, Random random) {
        Color auraColor = new Color(138, 43, 226, 100); // Semi-transparent purple
        g2d.setColor(auraColor);
        
        // Add aura particles around the wand
        for (int i = 0; i < 8; i++) {
            int x = 5 + random.nextInt(6);
            int y = 3 + random.nextInt(6);
            if (random.nextFloat() < 0.4f) {
                g2d.fillRect(x, y, 1, 1);
            }
        }
    }
    
    /**
     * Create advanced crystal pattern
     */
    private static void createAdvancedCrystalPattern(Graphics2D g2d, List<Color> colors, Random random, String pattern) {
        // Main crystal body
        Color crystalColor = colors.get(0);
        g2d.setColor(crystalColor);
        g2d.fillRect(6, 6, 4, 4);
        
        // Crystal facets
        if (colors.size() > 1) {
            g2d.setColor(colors.get(1));
            g2d.fillRect(5, 5, 6, 1);
            g2d.fillRect(5, 10, 6, 1);
            g2d.fillRect(5, 5, 1, 6);
            g2d.fillRect(10, 5, 1, 6);
        }
        
        // Highlights for crystalline shine
        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 6, 1, 1);
        g2d.fillRect(9, 6, 1, 1);
        g2d.fillRect(6, 9, 1, 1);
        g2d.fillRect(9, 9, 1, 1);
        
        // Additional sparkles if described as magical
        if (pattern.contains("magical") || pattern.contains("glowing")) {
            addCrystalSparkles(g2d, random);
        }
    }
    
    /**
     * Create advanced fire pattern
     */
    private static void createAdvancedFirePattern(Graphics2D g2d, List<Color> colors, Random random, String pattern) {
        // Base fire colors
        Color[] fireColors = {
            new Color(255, 69, 0),   // Red
            new Color(255, 140, 0),  // Orange
            new Color(255, 215, 0),  // Gold
            new Color(255, 255, 255) // White hot
        };
        
        // Create flame pattern from bottom up
        for (int y = 15; y >= 4; y--) {
            for (int x = 4; x < 12; x++) {
                float intensity = (16 - y) / 12.0f;
                if (random.nextFloat() < intensity * 0.7f) {
                    Color fireColor = fireColors[random.nextInt(fireColors.length)];
                    g2d.setColor(fireColor);
                    g2d.fillRect(x, y, 1, 1);
                }
            }
        }
        
        // Add extra intensity for "blazing" or "inferno" patterns
        if (pattern.contains("blazing") || pattern.contains("inferno")) {
            addIntenseFlames(g2d, random);
        }
    }
    
    /**
     * Create advanced ice pattern
     */
    private static void createAdvancedIcePattern(Graphics2D g2d, List<Color> colors, Random random, String pattern) {
        // Ice base
        Color iceColor = new Color(173, 216, 230);
        g2d.setColor(iceColor);
        g2d.fillRect(5, 5, 6, 6);
        
        // Ice crystals
        g2d.setColor(Color.WHITE);
        g2d.drawLine(4, 8, 11, 8);  // Horizontal
        g2d.drawLine(8, 4, 8, 11);  // Vertical
        g2d.drawLine(5, 5, 10, 10); // Diagonal 1
        g2d.drawLine(5, 10, 10, 5); // Diagonal 2
        
        // Frost details
        g2d.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 8; i++) {
            int x = 4 + random.nextInt(8);
            int y = 4 + random.nextInt(8);
            if (random.nextFloat() < 0.3f) {
                g2d.fillRect(x, y, 1, 1);
            }
        }
        
        // Additional frost effects for "frozen" patterns
        if (pattern.contains("frozen") || pattern.contains("glacial")) {
            addFrostEffects(g2d, random);
        }
    }
    
    /**
     * Create advanced generic pattern
     */
    private static void createAdvancedGenericPattern(Graphics2D g2d, List<Color> colors, Random random, String description, String pattern) {
        // Analyze description for pattern hints
        String lower = description.toLowerCase();
        
        if (lower.contains("geometric") || lower.contains("angular")) {
            createGeometricPattern(g2d, colors, random);
        } else if (lower.contains("organic") || lower.contains("natural")) {
            createOrganicPattern(g2d, colors, random);
        } else if (lower.contains("tech") || lower.contains("digital")) {
            createTechPattern(g2d, colors, random);
        } else {
            // Default layered pattern
            for (int i = 0; i < Math.min(colors.size(), 3); i++) {
                g2d.setColor(colors.get(i));
                int size = 6 - i * 2;
                int offset = 5 + i;
                g2d.fillRect(offset, offset, size, size);
            }
        }
    }
    
    /**
     * Add enhancement effects based on texture type
     */
    private static void addEnhancementEffects(Graphics2D g2d, List<Color> colors, String textureType, Random random) {
        switch (textureType.toLowerCase()) {
            case "sword":
            case "blade":
                addMetallicSheen(g2d, colors);
                break;
            case "wand":
            case "staff":
                addMagicalEffects(g2d, colors, random);
                break;
            case "crystal":
            case "gem":
                addCrystallineEffects(g2d, colors, random);
                break;
            default:
                addSubtleHighlights(g2d, colors);
                break;
        }
    }
    
    // Additional helper methods for specific effects
    private static void addCrystalSparkles(Graphics2D g2d, Random random) {
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < 4; i++) {
            int x = 3 + random.nextInt(10);
            int y = 3 + random.nextInt(10);
            if (random.nextFloat() < 0.5f) {
                g2d.fillRect(x, y, 1, 1);
            }
        }
    }
    
    private static void addIntenseFlames(Graphics2D g2d, Random random) {
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 6; i++) {
            int x = 5 + random.nextInt(6);
            int y = 4 + random.nextInt(4);
            g2d.fillRect(x, y, 1, 1);
        }
    }
    
    private static void addFrostEffects(Graphics2D g2d, Random random) {
        g2d.setColor(new Color(200, 230, 255, 120));
        for (int i = 0; i < 12; i++) {
            int x = 2 + random.nextInt(12);
            int y = 2 + random.nextInt(12);
            if (random.nextFloat() < 0.2f) {
                g2d.fillRect(x, y, 1, 1);
            }
        }
    }
    
    private static void createGeometricPattern(Graphics2D g2d, List<Color> colors, Random random) {
        for (int i = 0; i < colors.size() && i < 3; i++) {
            g2d.setColor(colors.get(i));
            // Create geometric shapes
            g2d.fillRect(5 + i, 5 + i, 6 - i * 2, 6 - i * 2);
        }
    }
    
    private static void createOrganicPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Create more organic, flowing patterns
        for (int i = 0; i < colors.size() && i < 3; i++) {
            g2d.setColor(colors.get(i));
            for (int j = 0; j < 20; j++) {
                int x = 4 + random.nextInt(8);
                int y = 4 + random.nextInt(8);
                if (random.nextFloat() < 0.6f) {
                    g2d.fillRect(x, y, 1, 1);
                }
            }
        }
    }
    
    private static void createTechPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Create digital/tech-like patterns
        g2d.setColor(colors.get(0));
        // Circuit-like lines
        g2d.drawLine(4, 8, 11, 8);
        g2d.drawLine(8, 4, 8, 11);
        g2d.drawLine(6, 6, 9, 9);
        
        // Tech nodes
        if (colors.size() > 1) {
            g2d.setColor(colors.get(1));
            g2d.fillRect(7, 7, 2, 2);
            g2d.fillRect(4, 8, 1, 1);
            g2d.fillRect(11, 8, 1, 1);
        }
    }
    
    private static void addMetallicSheen(Graphics2D g2d, List<Color> colors) {
        g2d.setColor(Color.WHITE);
        g2d.drawLine(7, 3, 7, 10);
        g2d.fillRect(8, 4, 1, 1);
        g2d.fillRect(8, 8, 1, 1);
    }
    
    private static void addMagicalEffects(Graphics2D g2d, List<Color> colors, Random random) {
        Color magicColor = new Color(138, 43, 226, 150);
        g2d.setColor(magicColor);
        for (int i = 0; i < 6; i++) {
            int x = 4 + random.nextInt(8);
            int y = 2 + random.nextInt(6);
            g2d.fillRect(x, y, 1, 1);
        }
    }
    
    private static void addCrystallineEffects(Graphics2D g2d, List<Color> colors, Random random) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 6, 1, 1);
        g2d.fillRect(9, 9, 1, 1);
        g2d.fillRect(6, 9, 1, 1);
        g2d.fillRect(9, 6, 1, 1);
    }
    
    private static void addSubtleHighlights(Graphics2D g2d, List<Color> colors) {
        if (colors.size() > 1) {
            Color highlight = colors.get(colors.size() - 1).brighter();
            g2d.setColor(highlight);
            g2d.fillRect(6, 6, 1, 1);
            g2d.fillRect(9, 9, 1, 1);
        }
    }
    
    /**
     * Texture generation options
     */
    public static class TextureGenerationOptions {
        public boolean useImageGeneration = true;
        public boolean useEnhancedPrompts = true;
        public String style = "";
        public Map<String, String> styleParameters = new HashMap<>();
        public int detailLevel = 3; // 1-5 scale
        public boolean cacheResults = true;
        
        public static TextureGenerationOptions defaultOptions() {
            return new TextureGenerationOptions();
        }
        
        public static TextureGenerationOptions highQuality() {
            TextureGenerationOptions options = new TextureGenerationOptions();
            options.detailLevel = 5;
            options.useEnhancedPrompts = true;
            return options;
        }
    }
    
    /**
     * Cached texture entry
     */
    private static class CachedTexture {
        private final ResourceLocation resourceLocation;
        private final long timestamp;
        private static final long CACHE_DURATION = 24 * 60 * 60 * 1000; // 24 hours
        
        public CachedTexture(ResourceLocation resourceLocation, long timestamp) {
            this.resourceLocation = resourceLocation;
            this.timestamp = timestamp;
        }
        
        public boolean isValid() {
            return System.currentTimeMillis() - timestamp < CACHE_DURATION;
        }
        
        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }
    }
    
    /**
     * Create cache key for texture caching
     */
    private static String createCacheKey(String name, String description, TextureGenerationOptions options) {
        return String.format("%s_%s_%s_%d", 
            name.hashCode(), 
            description.hashCode(), 
            options.style.hashCode(), 
            options.detailLevel);
    }
    
    /**
     * Parse texture response from AI
     */
    private static JsonObject parseTextureResponse(String response) {
        try {
            String jsonString = response;
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = response.substring(startIndex, endIndex + 1);
            }
            
            JsonObject parsed = gson.fromJson(jsonString, JsonObject.class);
            
            if (parsed != null && (parsed.has("colorPalette") || parsed.has("description") || parsed.has("base64"))) {
                return parsed;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse texture response as JSON: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Extract base64 image from response
     */
    private static String extractBase64FromResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        
        try {
            // Remove markdown code blocks
            response = response.replaceAll("```[a-zA-Z]*\\n?", "").replaceAll("```", "");
            
            // Look for data URL format
            if (response.contains("data:image")) {
                int commaIndex = response.indexOf(",");
                if (commaIndex != -1) {
                    response = response.substring(commaIndex + 1);
                }
            }
            
            // Clean up and extract base64
            String cleaned = response.replaceAll("[^A-Za-z0-9+/=\\n]", "").replaceAll("\\s", "");
            
            if (cleaned.length() > 100 && cleaned.length() % 4 == 0) {
                return cleaned;
            }
        } catch (Exception e) {
            LOGGER.warn("Error extracting base64: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Extract color palette from texture data
     */
    private static List<Color> extractColorPalette(JsonObject textureData) {
        List<Color> colors = new ArrayList<>();
        
        if (textureData.has("colorPalette")) {
            JsonArray colorArray = textureData.getAsJsonArray("colorPalette");
            for (int i = 0; i < colorArray.size(); i++) {
                try {
                    String hexColor = colorArray.get(i).getAsString();
                    if (hexColor.startsWith("#")) {
                        hexColor = hexColor.substring(1);
                    }
                    Color color = Color.decode("#" + hexColor);
                    colors.add(color);
                } catch (Exception e) {
                    LOGGER.warn("Invalid color in palette: {}", colorArray.get(i));
                }
            }
        }
        
        return colors;
    }
    
    /**
     * Save generated image to file
     */
    private static ResourceLocation saveGeneratedImage(String name, byte[] imageData, String type) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) {
                return null;
            }
            
            // Ensure 16x16 size
            if (image.getWidth() != 16 || image.getHeight() != 16) {
                BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(image, 0, 0, 16, 16, null);
                g2d.dispose();
                image = resized;
            }
            
            return saveTextureImage(name, image, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to save generated image: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Save texture image to file system
     */
    private static ResourceLocation saveTextureImage(String name, BufferedImage image, String type) {
        try {
            String directory = "src/main/resources/assets/" + AiModGenerator.MOD_ID + "/textures/" + type;
            Path dirPath = Paths.get(directory);
            Files.createDirectories(dirPath);

            File outputFile = new File(dirPath.toFile(), name + ".png");
            ImageIO.write(image, "PNG", outputFile);

            LOGGER.info("Generated enhanced texture for {} at {}", name, outputFile.getPath());
            return new ResourceLocation(AiModGenerator.MOD_ID, "textures/" + type + "/" + name);
            
        } catch (IOException e) {
            LOGGER.error("Failed to save texture: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate smart colors based on description analysis
     */
    private static List<Color> generateSmartColors(String description) {
        List<Color> colors = new ArrayList<>();
        String lower = description.toLowerCase();
        
        // Analyze description for color hints
        if (lower.contains("gold") || lower.contains("golden")) {
            colors.add(new Color(255, 215, 0));
            colors.add(new Color(218, 165, 32));
            colors.add(new Color(184, 134, 11));
        } else if (lower.contains("silver") || lower.contains("steel")) {
            colors.add(new Color(192, 192, 192));
            colors.add(new Color(169, 169, 169));
            colors.add(new Color(128, 128, 128));
        } else if (lower.contains("fire") || lower.contains("flame")) {
            colors.add(new Color(255, 69, 0));
            colors.add(new Color(255, 140, 0));
            colors.add(new Color(255, 215, 0));
        } else if (lower.contains("ice") || lower.contains("frost")) {
            colors.add(new Color(173, 216, 230));
            colors.add(new Color(135, 206, 250));
            colors.add(new Color(255, 255, 255));
        } else if (lower.contains("wood") || lower.contains("wooden")) {
            colors.add(new Color(139, 69, 19));
            colors.add(new Color(160, 82, 45));
            colors.add(new Color(210, 180, 140));
        } else if (lower.contains("crystal") || lower.contains("gem")) {
            colors.add(new Color(138, 43, 226));
            colors.add(new Color(75, 0, 130));
            colors.add(new Color(255, 20, 147));
        } else {
            // Default earth-tone palette
            colors.add(new Color(139, 69, 19));
            colors.add(new Color(160, 82, 45));
            colors.add(new Color(210, 180, 140));
        }
        
        return colors;
    }
}
