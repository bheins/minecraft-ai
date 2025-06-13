package com.example.aimodgen.generation;

import java.util.Map;

/**
 * Enhanced prompts and utilities for AI-based texture generation
 */
public class TexturePrompts {
    
    /**
     * Generate a comprehensive texture generation prompt
     */
    public static String createTexturePrompt(String description, String itemType) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Create a detailed specification for a 16x16 Minecraft-style pixel art texture.\n\n");
        prompt.append("ITEM DESCRIPTION: ").append(description).append("\n");
        prompt.append("ITEM TYPE: ").append(itemType).append("\n\n");
        
        prompt.append("RESPONSE FORMAT (JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"colorPalette\": [\"#hexcolor1\", \"#hexcolor2\", \"#hexcolor3\"],\n");
        prompt.append("  \"description\": \"detailed pixel-by-pixel description\",\n");
        prompt.append("  \"pattern\": \"visual pattern description\",\n");
        prompt.append("  \"style\": \"minecraft texture style notes\",\n");
        prompt.append("  \"dominantColors\": [\"primary\", \"secondary\", \"accent\"],\n");
        prompt.append("  \"textureType\": \"sword/wand/crystal/generic/etc\",\n");
        prompt.append("  \"base64\": \"optional base64 PNG data if available\"\n");
        prompt.append("}\n\n");
        
        prompt.append("REQUIREMENTS:\n");
        prompt.append("- 16x16 pixel resolution\n");
        prompt.append("- Minecraft's blocky, pixelated aesthetic\n");
        prompt.append("- Clear, recognizable shapes at small resolution\n");
        prompt.append("- Appropriate colors for the described item\n");
        prompt.append("- Simple but distinctive patterns\n");
        prompt.append("- Consider lighting and depth with darker/lighter variants\n\n");
        
        prompt.append("STYLE GUIDELINES:\n");
        prompt.append("- Use flat colors with minimal gradients\n");
        prompt.append("- Add subtle shading with darker color variants\n");
        prompt.append("- Include highlights with lighter variants or white\n");
        prompt.append("- Keep details bold and visible at 16x16 scale\n");
        prompt.append("- Use black or dark outlines for definition\n\n");
        
        // Add specific guidance based on item type
        addSpecificGuidance(prompt, description.toLowerCase(), itemType);
        
        return prompt.toString();
    }
    
    /**
     * Add specific guidance based on item description
     */
    private static void addSpecificGuidance(StringBuilder prompt, String lowerDescription, String itemType) {
        prompt.append("SPECIFIC GUIDANCE FOR THIS ITEM:\n");
        
        if (lowerDescription.contains("sword") || lowerDescription.contains("blade")) {
            prompt.append("- Create a sword silhouette with blade, crossguard, and handle\n");
            prompt.append("- Use metallic colors (silver, gold, or described material)\n");
            prompt.append("- Add a shine line down the blade center\n");
            prompt.append("- Make the handle darker than the blade\n");
        } else if (lowerDescription.contains("wand") || lowerDescription.contains("staff")) {
            prompt.append("- Design a staff/wand with distinct handle and magical tip\n");
            prompt.append("- Use wood tones for handle, bright colors for magical elements\n");
            prompt.append("- Add sparkle effects or glowing orbs\n");
            prompt.append("- Consider adding small magical particles around the tip\n");
        } else if (lowerDescription.contains("crystal") || lowerDescription.contains("gem")) {
            prompt.append("- Create faceted crystal shapes with geometric faces\n");
            prompt.append("- Use bright, saturated colors with transparency effects\n");
            prompt.append("- Add multiple highlight spots for crystalline shine\n");
            prompt.append("- Consider angular, geometric patterns\n");
        } else if (lowerDescription.contains("fire") || lowerDescription.contains("flame")) {
            prompt.append("- Use warm colors: reds, oranges, yellows\n");
            prompt.append("- Create flame-like flowing patterns\n");
            prompt.append("- Add bright yellow/white highlights for heat\n");
            prompt.append("- Consider ember particles or glow effects\n");
        } else if (lowerDescription.contains("ice") || lowerDescription.contains("frost")) {
            prompt.append("- Use cool colors: blues, whites, light cyans\n");
            prompt.append("- Create crystalline, angular ice patterns\n");
            prompt.append("- Add white highlights for ice shine\n");
            prompt.append("- Consider snowflake or frost patterns\n");
        } else if (lowerDescription.contains("gold") || lowerDescription.contains("golden")) {
            prompt.append("- Use gold color palette: #FFD700, #DAA520, #B8860B\n");
            prompt.append("- Add metallic shine effects\n");
            prompt.append("- Create reflective highlights\n");
            prompt.append("- Consider ornate decorative patterns\n");
        } else if (lowerDescription.contains("magic") || lowerDescription.contains("mystical")) {
            prompt.append("- Use mystical colors: purples, blues, deep greens\n");
            prompt.append("- Add glowing or luminous effects\n");
            prompt.append("- Consider runes, symbols, or arcane patterns\n");
            prompt.append("- Add sparkle or energy effects\n");
        } else {
            prompt.append("- Focus on the core material and function described\n");
            prompt.append("- Use colors appropriate to the item's purpose\n");
            prompt.append("- Add realistic shading and highlights\n");
            prompt.append("- Keep the design simple but recognizable\n");
        }
        
        prompt.append("\nEnsure the final design is clear and appealing at 16x16 pixel scale!\n");
    }
    
    /**
     * Create a fallback prompt for simpler AI models
     */
    public static String createSimpleTexturePrompt(String description) {
        return String.format(
            "Describe a 16x16 Minecraft pixel art texture for '%s'. " +
            "List 3-5 main colors as hex codes and describe the visual pattern. " +
            "Focus on Minecraft's blocky style and keep it simple for small resolution.",
            description
        );
    }
    
    /**
     * Create a color extraction prompt
     */
    public static String createColorPrompt(String description) {
        return String.format(
            "What colors should be used for a Minecraft item representing '%s'? " +
            "Provide 3-5 hex color codes that would be appropriate. " +
            "Consider the material, function, and Minecraft's color palette.",
            description
        );
    }

    /**
     * Create a styled texture prompt with specific style parameters
     */
    public static String createStyledTexturePrompt(String description, String style, Map<String, String> styleParameters) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Create a 16x16 Minecraft-style pixel art texture with SPECIFIC STYLE.\n\n");
        prompt.append("ITEM DESCRIPTION: ").append(description).append("\n");
        prompt.append("REQUIRED STYLE: ").append(style).append("\n\n");
        
        if (styleParameters != null && !styleParameters.isEmpty()) {
            prompt.append("STYLE PARAMETERS:\n");
            for (Map.Entry<String, String> param : styleParameters.entrySet()) {
                prompt.append("- ").append(param.getKey()).append(": ").append(param.getValue()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("RESPONSE FORMAT (JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"colorPalette\": [\"#hexcolor1\", \"#hexcolor2\", \"#hexcolor3\"],\n");
        prompt.append("  \"description\": \"detailed pixel-by-pixel description\",\n");
        prompt.append("  \"style\": \"").append(style).append("\",\n");
        prompt.append("  \"pattern\": \"visual pattern description\",\n");
        prompt.append("  \"base64\": \"optional base64 PNG data if available\"\n");
        prompt.append("}\n\n");
        
        // Add style-specific guidance
        addStyleSpecificGuidance(prompt, style, styleParameters);
        
        return prompt.toString();
    }

    /**
     * Create a variation prompt for generating multiple options
     */
    public static String createVariationPrompt(String description, String itemType, int variationIndex) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Create VARIATION #").append(variationIndex + 1).append(" of a 16x16 Minecraft texture.\n\n");
        prompt.append("ITEM DESCRIPTION: ").append(description).append("\n");
        prompt.append("ITEM TYPE: ").append(itemType).append("\n");
        prompt.append("VARIATION GOAL: Create a unique interpretation while staying true to the description\n\n");
        
        // Add variation-specific instructions
        switch (variationIndex % 3) {
            case 0:
                prompt.append("VARIATION FOCUS: Bold, high-contrast design\n");
                prompt.append("- Use vibrant, saturated colors\n");
                prompt.append("- Create strong visual contrast\n");
                prompt.append("- Make details bold and prominent\n");
                break;
            case 1:
                prompt.append("VARIATION FOCUS: Subtle, refined design\n");
                prompt.append("- Use muted, sophisticated colors\n");
                prompt.append("- Create gentle gradations\n");
                prompt.append("- Focus on elegant details\n");
                break;
            case 2:
                prompt.append("VARIATION FOCUS: Unique, creative interpretation\n");
                prompt.append("- Experiment with unexpected color combinations\n");
                prompt.append("- Try innovative patterns or textures\n");
                prompt.append("- Add distinctive creative elements\n");
                break;
        }
        
        prompt.append("\nRESPONSE FORMAT (JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"colorPalette\": [\"#hexcolor1\", \"#hexcolor2\", \"#hexcolor3\"],\n");
        prompt.append("  \"description\": \"detailed pixel-by-pixel description\",\n");
        prompt.append("  \"variation\": ").append(variationIndex + 1).append(",\n");
        prompt.append("  \"pattern\": \"visual pattern description\",\n");
        prompt.append("  \"base64\": \"optional base64 PNG data if available\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    /**
     * Add style-specific guidance
     */
    private static void addStyleSpecificGuidance(StringBuilder prompt, String style, Map<String, String> styleParameters) {
        prompt.append("STYLE-SPECIFIC REQUIREMENTS:\n");
        
        switch (style.toLowerCase()) {
            case "realistic":
                prompt.append("- Use natural, realistic colors and textures\n");
                prompt.append("- Add realistic lighting and shadows\n");
                prompt.append("- Focus on material authenticity\n");
                break;
            case "fantasy":
                prompt.append("- Use magical, vibrant colors\n");
                prompt.append("- Add mystical effects and glowing elements\n");
                prompt.append("- Include fantastical details\n");
                break;
            case "medieval":
                prompt.append("- Use earthy, muted colors\n");
                prompt.append("- Add worn, aged textures\n");
                prompt.append("- Include medieval design elements\n");
                break;
            case "futuristic":
                prompt.append("- Use bright, tech-inspired colors\n");
                prompt.append("- Add geometric, digital patterns\n");
                prompt.append("- Include sci-fi design elements\n");
                break;
            case "minimalist":
                prompt.append("- Use simple, clean color palette\n");
                prompt.append("- Keep patterns minimal and geometric\n");
                prompt.append("- Focus on essential design elements only\n");
                break;
            default:
                prompt.append("- Follow the specified style parameters\n");
                prompt.append("- Maintain consistency with the requested aesthetic\n");
                break;
        }
        
        prompt.append("\nEnsure the style is clearly visible in the final 16x16 texture!\n");
    }

    /**
     * Create an enhancement prompt for refining existing textures
     */
    public static String createEnhancementPrompt(String description, String currentTexture, String[] improvementAreas) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("TEXTURE ENHANCEMENT REQUEST\n\n");
        prompt.append("ORIGINAL DESCRIPTION: ").append(description).append("\n");
        prompt.append("CURRENT TEXTURE DATA: ").append(currentTexture).append("\n\n");
        
        prompt.append("IMPROVEMENT AREAS:\n");
        for (String area : improvementAreas) {
            prompt.append("- ").append(area).append("\n");
        }
        prompt.append("\n");
        
        prompt.append("Please enhance the texture by:\n");
        prompt.append("1. Improving color harmony and visual appeal\n");
        prompt.append("2. Adding better contrast and definition\n");
        prompt.append("3. Enhancing details while maintaining 16x16 clarity\n");
        prompt.append("4. Optimizing for Minecraft's aesthetic\n");
        prompt.append("5. Making it more distinctive and memorable\n\n");
        
        prompt.append("Return enhanced JSON with improved specifications.\n");
        
        return prompt.toString();
    }
}
