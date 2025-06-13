package com.example.aimodgen.integration;

import com.example.aimodgen.generation.AdvancedTextureGenerator;
import com.example.aimodgen.generation.ContentGenerator;
import com.example.aimodgen.generation.GeneratedContent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced AI-powered content generation service that integrates advanced texture generation
 * with improved user description processing and multiple generation strategies.
 */
public class EnhancedContentGenerationService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static EnhancedContentGenerationService instance;
    
    private final ContentGenerator contentGenerator;
    private final Map<String, TextureGenerationProfile> userProfiles;
      private EnhancedContentGenerationService() {
        this.contentGenerator = ContentGenerator.getInstance();
        this.userProfiles = new HashMap<>();
    }
    
    public static EnhancedContentGenerationService getInstance() {
        if (instance == null) {
            instance = new EnhancedContentGenerationService();
        }
        return instance;
    }    /**
     * Generate content with enhanced AI capabilities
     */
    public CompletableFuture<GeneratedContent> generateEnhancedContent(String description, EnhancedGenerationOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("Starting enhanced content generation for: {}", description);
                
                // Analyze the description for better understanding
                DescriptionAnalysis analysis = analyzeDescription(description);
                
                // Generate base content using existing system
                CompletableFuture<GeneratedContent> contentFuture = generateBaseContent(description, analysis);
                GeneratedContent baseContent = contentFuture.get();
                
                if (baseContent == null) {
                    throw new RuntimeException("Failed to generate base content");
                }
                
                // Enhance the texture using advanced generation
                if (options.enhanceTextures) {
                    enhanceContentTexture(baseContent, analysis, options);
                }
                
                // Apply user preferences if available
                if (options.userId != null) {
                    applyUserPreferences(baseContent, options.userId, analysis);
                }
                
                LOGGER.info("Enhanced content generation completed for: {}", description);
                return baseContent;
                
            } catch (Exception e) {
                LOGGER.error("Enhanced content generation failed for '{}': {}", description, e.getMessage());
                // Fallback to basic generation
                try {
                    CompletableFuture<GeneratedContent> fallbackFuture = generateBasicFallback(description);
                    return fallbackFuture.get();
                } catch (Exception fallbackError) {
                    LOGGER.error("Fallback generation also failed: {}", fallbackError.getMessage());
                    throw new RuntimeException("All content generation attempts failed", e);
                }
            }
        });
    }
    
    /**
     * Analyze description to extract key characteristics
     */
    private DescriptionAnalysis analyzeDescription(String description) {
        DescriptionAnalysis analysis = new DescriptionAnalysis();
        String lower = description.toLowerCase();
        
        // Analyze item type
        if (lower.contains("sword") || lower.contains("blade") || lower.contains("knife")) {
            analysis.itemType = "weapon";
            analysis.subType = "sword";
        } else if (lower.contains("wand") || lower.contains("staff") || lower.contains("rod")) {
            analysis.itemType = "magic";
            analysis.subType = "wand";
        } else if (lower.contains("pickaxe") || lower.contains("axe") || lower.contains("shovel")) {
            analysis.itemType = "tool";
            analysis.subType = extractToolType(lower);
        } else if (lower.contains("armor") || lower.contains("helmet") || lower.contains("chestplate")) {
            analysis.itemType = "armor";
            analysis.subType = extractArmorType(lower);
        } else {
            analysis.itemType = "generic";
            analysis.subType = "item";
        }
        
        // Analyze materials
        analysis.material = extractMaterial(lower);
        
        // Analyze magical properties
        analysis.isMagical = lower.contains("magic") || lower.contains("enchanted") || lower.contains("mystical") || 
                           lower.contains("arcane") || lower.contains("elemental");
        
        // Analyze elemental type
        if (lower.contains("fire") || lower.contains("flame") || lower.contains("blaze")) {
            analysis.element = "fire";
        } else if (lower.contains("ice") || lower.contains("frost") || lower.contains("frozen")) {
            analysis.element = "ice";
        } else if (lower.contains("lightning") || lower.contains("thunder") || lower.contains("electric")) {
            analysis.element = "lightning";
        } else if (lower.contains("earth") || lower.contains("stone") || lower.contains("rock")) {
            analysis.element = "earth";
        }
        
        // Analyze complexity
        String[] complexWords = {"ornate", "decorated", "intricate", "detailed", "elaborate"};
        for (String word : complexWords) {
            if (lower.contains(word)) {
                analysis.complexityLevel = Math.min(5, analysis.complexityLevel + 1);
            }
        }
        
        // Analyze style hints
        if (lower.contains("ancient") || lower.contains("old") || lower.contains("weathered")) {
            analysis.style = "ancient";
        } else if (lower.contains("modern") || lower.contains("futuristic") || lower.contains("tech")) {
            analysis.style = "futuristic";
        } else if (lower.contains("royal") || lower.contains("noble") || lower.contains("ornate")) {
            analysis.style = "royal";
        } else {
            analysis.style = "standard";
        }
        
        return analysis;
    }
      /**
     * Generate base content using existing content generation system
     */
    private CompletableFuture<GeneratedContent> generateBaseContent(String description, DescriptionAnalysis analysis) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use the existing content generator but with enhanced context
                Map<String, String> properties = new HashMap<>();
                properties.put("type", analysis.itemType);
                properties.put("subType", analysis.subType);
                properties.put("material", analysis.material);
                properties.put("element", analysis.element);
                properties.put("style", analysis.style);
                properties.put("complexity", String.valueOf(analysis.complexityLevel));
                
                // Generate item and then extract the GeneratedContent from the content generator's internal map
                contentGenerator.generateItem(description, properties).get();
                
                // Access the generated content from the content generator
                // We need to find the content that was just generated
                Map<String, GeneratedContent> allContent = contentGenerator.listContent();
                
                // Find the most recently generated content matching our description
                GeneratedContent result = allContent.values().stream()
                    .filter(content -> content.getDescription().equals(description))
                    .reduce((first, second) -> second) // Get the last one (most recent)
                    .orElse(null);
                
                return result;
            } catch (Exception e) {
                LOGGER.error("Base content generation failed: {}", e.getMessage());
                return null;
            }
        });
    }
    
    /**
     * Generate basic fallback content
     */
    private CompletableFuture<GeneratedContent> generateBasicFallback(String description) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simple fallback generation
                Map<String, String> basicProperties = new HashMap<>();
                basicProperties.put("type", "generic");
                basicProperties.put("fallback", "true");
                
                contentGenerator.generateItem(description, basicProperties).get();
                
                // Return the generated content
                Map<String, GeneratedContent> allContent = contentGenerator.listContent();
                return allContent.values().stream()
                    .filter(content -> content.getDescription().equals(description))
                    .reduce((first, second) -> second)
                    .orElse(null);
                    
            } catch (Exception e) {
                LOGGER.error("Fallback content generation failed: {}", e.getMessage());
                return null;
            }
        });
    }
    
    /**
     * Enhance content texture using advanced generation
     */
    private void enhanceContentTexture(GeneratedContent content, DescriptionAnalysis analysis, EnhancedGenerationOptions options) {
        try {
            AdvancedTextureGenerator.TextureGenerationOptions textureOptions = 
                AdvancedTextureGenerator.TextureGenerationOptions.defaultOptions();
            
            // Configure texture options based on analysis
            textureOptions.style = analysis.style;
            textureOptions.detailLevel = analysis.complexityLevel;
            textureOptions.useEnhancedPrompts = options.useAdvancedAI;
            
            // Add style parameters
            if (analysis.isMagical) {
                textureOptions.styleParameters.put("magical", "true");
                textureOptions.styleParameters.put("glowing", "true");
            }
            if (analysis.element != null) {
                textureOptions.styleParameters.put("element", analysis.element);
            }
            if (analysis.material != null) {
                textureOptions.styleParameters.put("material", analysis.material);
            }
            
            // Generate enhanced texture
            CompletableFuture<ResourceLocation> textureGeneration = 
                AdvancedTextureGenerator.generateEnhancedTexture(
                    content.getId(), 
                    content.getDescription(), 
                    "item", 
                    textureOptions
                );
            
            // Wait for texture generation to complete
            ResourceLocation enhancedTexture = textureGeneration.get();
            if (enhancedTexture != null) {
                LOGGER.info("Enhanced texture generated for: {}", content.getId());
            }
            
        } catch (Exception e) {
            LOGGER.warn("Texture enhancement failed for {}: {}", content.getId(), e.getMessage());
            // Texture enhancement failure doesn't break the overall process
        }
    }
    
    /**
     * Apply user preferences to generated content
     */
    private void applyUserPreferences(GeneratedContent content, String userId, DescriptionAnalysis analysis) {
        TextureGenerationProfile profile = userProfiles.get(userId);
        if (profile == null) {
            profile = new TextureGenerationProfile();
            userProfiles.put(userId, profile);
        }
        
        // Learn from this generation
        profile.recordGeneration(analysis.itemType, analysis.style, analysis.material);
        
        // Apply preferences could include color scheme adjustments, style preferences, etc.
        LOGGER.debug("Applied user preferences for user: {}", userId);
    }
    
    // Helper methods for description analysis
    private String extractToolType(String description) {
        if (description.contains("pickaxe")) return "pickaxe";
        if (description.contains("axe")) return "axe";
        if (description.contains("shovel")) return "shovel";
        if (description.contains("hoe")) return "hoe";
        return "tool";
    }
    
    private String extractArmorType(String description) {
        if (description.contains("helmet")) return "helmet";
        if (description.contains("chestplate")) return "chestplate";
        if (description.contains("leggings")) return "leggings";
        if (description.contains("boots")) return "boots";
        return "armor";
    }
    
    private String extractMaterial(String description) {
        if (description.contains("diamond")) return "diamond";
        if (description.contains("gold") || description.contains("golden")) return "gold";
        if (description.contains("iron")) return "iron";
        if (description.contains("steel")) return "steel";
        if (description.contains("wood") || description.contains("wooden")) return "wood";
        if (description.contains("stone")) return "stone";
        if (description.contains("obsidian")) return "obsidian";
        if (description.contains("crystal")) return "crystal";
        if (description.contains("mithril")) return "mithril";
        if (description.contains("adamant")) return "adamant";
        return "unknown";
    }
    
    /**
     * Enhanced generation options
     */
    public static class EnhancedGenerationOptions {
        public boolean enhanceTextures = true;
        public boolean useAdvancedAI = true;
        public boolean learnFromUser = true;
        public String userId = null;
        public int qualityLevel = 3; // 1-5 scale
        
        public static EnhancedGenerationOptions defaultOptions() {
            return new EnhancedGenerationOptions();
        }
        
        public static EnhancedGenerationOptions highQuality() {
            EnhancedGenerationOptions options = new EnhancedGenerationOptions();
            options.qualityLevel = 5;
            options.useAdvancedAI = true;
            options.enhanceTextures = true;
            return options;
        }
        
        public EnhancedGenerationOptions withUser(String userId) {
            this.userId = userId;
            this.learnFromUser = true;
            return this;
        }
    }
    
    /**
     * Description analysis result
     */
    private static class DescriptionAnalysis {
        public String itemType = "generic";
        public String subType = "item";
        public String material = "unknown";
        public String element = null;
        public String style = "standard";
        public boolean isMagical = false;
        public int complexityLevel = 1;
    }
    
    /**
     * User texture generation profile for learning preferences
     */
    private static class TextureGenerationProfile {
        private final Map<String, Integer> itemTypePreferences = new HashMap<>();
        private final Map<String, Integer> stylePreferences = new HashMap<>();
        private final Map<String, Integer> materialPreferences = new HashMap<>();
        
        public void recordGeneration(String itemType, String style, String material) {
            itemTypePreferences.merge(itemType, 1, Integer::sum);
            stylePreferences.merge(style, 1, Integer::sum);
            materialPreferences.merge(material, 1, Integer::sum);
        }
          @SuppressWarnings("unused")
        public String getPreferredStyle() {
            return stylePreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("standard");
        }
        
        @SuppressWarnings("unused")
        public String getPreferredMaterial() {
            return materialPreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
        }
    }
}
