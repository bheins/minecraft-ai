package com.example.aimodgen.generation;

import com.example.aimodgen.AiModGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson gson = new Gson();
    
    public static ResourceLocation generateTexture(String name, String description, String type) {
        try {
            // Generate texture using AI
            String aiResponse = AiModGenerator.getInstance().getLlmService().generateTexture(description);
            if (aiResponse == null || aiResponse.isEmpty()) {
                LOGGER.warn("AI failed to generate texture for {}, using advanced fallback", name);
                return generateAdvancedTexture(name, description, type);
            }

            // Try to parse AI response as JSON first
            JsonObject textureData = parseTextureResponse(aiResponse);
            if (textureData != null) {
                // Generate texture from AI description
                ResourceLocation result = generateFromAIDescription(name, description, textureData, type);
                if (result != null) {
                    return result;
                }
            }

            // Extract base64 from AI response as fallback
            String base64Image = extractBase64(aiResponse);
            if (base64Image != null) {
                return generateFromBase64(name, base64Image, type);
            }

            LOGGER.warn("Could not process AI response for {}, using advanced fallback", name);
            return generateAdvancedTexture(name, description, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to generate texture for {}: {}", name, e.getMessage());            return generateAdvancedTexture(name, description, type);
        }
    }
    
    /**
     * Parse AI response to extract texture description JSON
     */
    private static JsonObject parseTextureResponse(String response) {
        try {
            // Try to extract JSON from response
            String jsonString = response;
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = response.substring(startIndex, endIndex + 1);
            }
            
            JsonObject parsed = gson.fromJson(jsonString, JsonObject.class);
            
            // Validate that it has texture-related fields
            if (parsed != null && (parsed.has("colorPalette") || parsed.has("description") || parsed.has("base64"))) {
                return parsed;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse texture response as JSON: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Generate texture from AI description and color palette
     */
    private static ResourceLocation generateFromAIDescription(String name, String description, JsonObject textureData, String type) {
        try {
            // Extract color palette
            List<Color> colorPalette = new ArrayList<>();
            if (textureData.has("colorPalette")) {
                JsonArray colors = textureData.getAsJsonArray("colorPalette");
                for (int i = 0; i < colors.size(); i++) {
                    try {
                        String hexColor = colors.get(i).getAsString();
                        if (hexColor.startsWith("#")) {
                            hexColor = hexColor.substring(1);
                        }
                        Color color = Color.decode("#" + hexColor);
                        colorPalette.add(color);
                    } catch (Exception e) {
                        LOGGER.warn("Invalid color in palette: {}", colors.get(i));
                    }
                }
            }
            
            // If no valid colors, generate based on description
            if (colorPalette.isEmpty()) {
                colorPalette = generateColorsFromDescription(description);
            }
            
            // Create texture from colors and description
            BufferedImage image = createMinecraftStyleTexture(name, description, colorPalette);
            
            // Save the image
            return saveTexture(name, image, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to generate texture from AI description: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate texture from base64 data
     */
    private static ResourceLocation generateFromBase64(String name, String base64Image, String type) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            
            if (image == null) {
                return null;
            }
            
            // Ensure it's 16x16 and in the right format
            if (image.getWidth() != 16 || image.getHeight() != 16) {
                BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(image, 0, 0, 16, 16, null);
                g2d.dispose();
                image = resized;
            }
            
            return saveTexture(name, image, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to generate texture from base64: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate colors based on description keywords
     */
    private static List<Color> generateColorsFromDescription(String description) {
        List<Color> colors = new ArrayList<>();
        String lowerDesc = description.toLowerCase();
        
        // Material-based colors
        if (lowerDesc.contains("gold") || lowerDesc.contains("golden")) {
            colors.add(new Color(255, 215, 0));  // Gold
            colors.add(new Color(218, 165, 32));  // Goldenrod
        }
        if (lowerDesc.contains("iron") || lowerDesc.contains("metal")) {
            colors.add(new Color(192, 192, 192));  // Silver
            colors.add(new Color(128, 128, 128));  // Gray
        }
        if (lowerDesc.contains("diamond")) {
            colors.add(new Color(185, 242, 255));  // Light blue
            colors.add(new Color(64, 224, 255));   // Deep sky blue
        }
        if (lowerDesc.contains("emerald") || lowerDesc.contains("green")) {
            colors.add(new Color(50, 205, 50));    // Lime green
            colors.add(new Color(0, 128, 0));      // Dark green
        }
        if (lowerDesc.contains("fire") || lowerDesc.contains("flame")) {
            colors.add(new Color(255, 69, 0));     // Red orange
            colors.add(new Color(255, 140, 0));    // Dark orange
            colors.add(new Color(255, 215, 0));    // Gold
        }
        if (lowerDesc.contains("ice") || lowerDesc.contains("frost")) {
            colors.add(new Color(173, 216, 230));  // Light blue
            colors.add(new Color(135, 206, 250));  // Light sky blue
            colors.add(new Color(255, 255, 255));  // White
        }
        if (lowerDesc.contains("magic") || lowerDesc.contains("mystical")) {
            colors.add(new Color(138, 43, 226));   // Blue violet
            colors.add(new Color(75, 0, 130));     // Indigo
            colors.add(new Color(255, 20, 147));   // Deep pink
        }
        
        // If no specific colors found, use default palette
        if (colors.isEmpty()) {
            colors.add(new Color(139, 69, 19));    // Saddle brown
            colors.add(new Color(160, 82, 45));    // Sienna
            colors.add(new Color(210, 180, 140));  // Tan
        }
        
        return colors;
    }
    
    /**
     * Create a Minecraft-style 16x16 texture
     */
    private static BufferedImage createMinecraftStyleTexture(String name, String description, List<Color> colorPalette) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        Random random = new Random(name.hashCode()); // Consistent randomness based on name
        
        // Fill background with main color
        Color mainColor = colorPalette.get(0);
        g2d.setColor(mainColor);
        g2d.fillRect(0, 0, 16, 16);
        
        // Add pattern based on description
        String lowerDesc = description.toLowerCase();
        
        if (lowerDesc.contains("sword") || lowerDesc.contains("blade")) {
            createSwordPattern(g2d, colorPalette, random);
        } else if (lowerDesc.contains("wand") || lowerDesc.contains("staff")) {
            createWandPattern(g2d, colorPalette, random);
        } else if (lowerDesc.contains("crystal") || lowerDesc.contains("gem")) {
            createCrystalPattern(g2d, colorPalette, random);
        } else if (lowerDesc.contains("fire") || lowerDesc.contains("flame")) {
            createFirePattern(g2d, colorPalette, random);
        } else if (lowerDesc.contains("ice") || lowerDesc.contains("frost")) {
            createIcePattern(g2d, colorPalette, random);
        } else {
            createGenericPattern(g2d, colorPalette, random);
        }
        
        // Add border for definition
        g2d.setColor(new Color(0, 0, 0, 128)); // Semi-transparent black
        g2d.drawRect(0, 0, 15, 15);
        
        g2d.dispose();
        return image;
    }
    
    private static void createSwordPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Draw sword blade
        Color bladeColor = colors.size() > 1 ? colors.get(1) : colors.get(0).brighter();
        g2d.setColor(bladeColor);
        g2d.fillRect(7, 2, 2, 10);
        
        // Draw hilt
        Color hiltColor = colors.size() > 2 ? colors.get(2) : colors.get(0).darker();
        g2d.setColor(hiltColor);
        g2d.fillRect(5, 12, 6, 2);
        
        // Add some shine
        g2d.setColor(Color.WHITE);
        g2d.fillRect(7, 3, 1, 8);
    }
    
    private static void createWandPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Draw wand handle
        Color handleColor = colors.size() > 1 ? colors.get(1) : colors.get(0).darker();
        g2d.setColor(handleColor);
        g2d.fillRect(7, 8, 2, 6);
        
        // Draw wand tip
        Color tipColor = colors.size() > 2 ? colors.get(2) : Color.WHITE;
        g2d.setColor(tipColor);
        g2d.fillRect(6, 6, 4, 3);
        
        // Add sparkles
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(16);
            int y = random.nextInt(8);
            g2d.fillRect(x, y, 1, 1);
        }
    }
    
    private static void createCrystalPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Draw crystal facets
        for (int i = 0; i < colors.size() && i < 3; i++) {
            g2d.setColor(colors.get(i));
            int size = 6 - i * 2;
            int offset = i + 5;
            g2d.fillRect(offset, offset, size, size);
        }
        
        // Add highlights
        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 6, 1, 4);
        g2d.fillRect(9, 6, 1, 4);
    }
    
    private static void createFirePattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Create flame-like pattern
        for (int y = 12; y >= 4; y--) {
            for (int x = 4; x < 12; x++) {
                if (random.nextFloat() < 0.6f) {
                    Color fireColor = colors.get(random.nextInt(colors.size()));
                    g2d.setColor(fireColor);
                    g2d.fillRect(x, y, 1, 1);
                }
            }
        }
    }
    
    private static void createIcePattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Create ice crystal pattern
        g2d.setColor(colors.get(0));
        g2d.fillRect(6, 6, 4, 4);
        
        // Add ice shards
        g2d.setColor(Color.WHITE);
        g2d.drawLine(4, 8, 11, 8);
        g2d.drawLine(8, 4, 8, 11);
        g2d.drawLine(5, 5, 10, 10);
        g2d.drawLine(5, 10, 10, 5);
    }
    
    private static void createGenericPattern(Graphics2D g2d, List<Color> colors, Random random) {
        // Create a simple geometric pattern
        for (int i = 0; i < colors.size() && i < 4; i++) {
            g2d.setColor(colors.get(i));
            int size = 3 + i;
            int offset = 6 - i;
            g2d.fillRect(offset, offset, size, size);
        }
    }
    
    /**
     * Save texture image to file
     */
    private static ResourceLocation saveTexture(String name, BufferedImage image, String type) {
        try {
            // Ensure resources directory exists
            String directory = "src/main/resources/assets/" + AiModGenerator.MOD_ID + "/textures/" + type;
            Path dirPath = Paths.get(directory);
            Files.createDirectories(dirPath);

            // Save image
            File outputFile = new File(dirPath.toFile(), name + ".png");
            ImageIO.write(image, "PNG", outputFile);

            LOGGER.info("Generated texture for {} at {}", name, outputFile.getPath());
            return new ResourceLocation(AiModGenerator.MOD_ID, "textures/" + type + "/" + name);
            
        } catch (IOException e) {
            LOGGER.error("Failed to save texture: {}", e.getMessage());
            return null;
        }
    }
      /**
     * Generate an advanced fallback texture when AI fails
     */
    public static ResourceLocation generateAdvancedTexture(String name, String description, String type) {
        try {
            List<Color> colors = generateColorsFromDescription(description);
            BufferedImage image = createMinecraftStyleTexture(name, description, colors);
            return saveTexture(name, image, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to generate advanced texture, using simple fallback: {}", e.getMessage());            return generateSimpleTexture(name, type);
        }
    }
    private static String extractBase64(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        
        try {
            // Remove any markdown code blocks
            response = response.replaceAll("```[a-zA-Z]*\\n?", "").replaceAll("```", "");
            
            // Look for data URL format (data:image/png;base64,...)
            Pattern dataUrlPattern = Pattern.compile("data:image/[^;]+;base64,([A-Za-z0-9+/=]+)", Pattern.DOTALL);
            Matcher dataUrlMatcher = dataUrlPattern.matcher(response);
            if (dataUrlMatcher.find()) {
                return dataUrlMatcher.group(1);
            }
            
            // Look for standalone base64 (lines of base64 characters)
            Pattern base64Pattern = Pattern.compile("([A-Za-z0-9+/]{20,}={0,2})", Pattern.DOTALL);
            Matcher base64Matcher = base64Pattern.matcher(response);
            if (base64Matcher.find()) {
                String candidate = base64Matcher.group(1);
                // Remove any whitespace
                candidate = candidate.replaceAll("\\s", "");
                // Check if it's a reasonable length for an image
                if (candidate.length() > 100) {
                    return candidate;
                }
            }
            
            // Try the entire response after cleaning
            String cleaned = response.replaceAll("[^A-Za-z0-9+/=]", "");
            if (cleaned.length() > 100) {
                return cleaned;
            }
            
        } catch (Exception e) {
            LOGGER.warn("Error extracting base64: {}", e.getMessage());
        }
        
        return null;
    }
      /**
     * Generate a simple default texture when AI generation fails
     */
    private static ResourceLocation generateSimpleTexture(String name, String type) {
        try {
            // Create a simple 16x16 colored square
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // Use name hash to generate a consistent color
            int colorValue = Math.abs(name.hashCode()) % 0xFFFFFF;
            Color color = new Color(colorValue);
            g2d.setColor(color);
            g2d.fillRect(0, 0, 16, 16);
            
            // Add a border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, 15, 15);
            g2d.dispose();

            // Ensure resources directory exists
            String directory = "src/main/resources/assets/" + AiModGenerator.MOD_ID + "/textures/" + type;
            Path dirPath = Paths.get(directory);
            Files.createDirectories(dirPath);

            // Save default image
            File outputFile = new File(dirPath.toFile(), name + ".png");
            ImageIO.write(image, "PNG", outputFile);

            LOGGER.info("Generated default texture for {} at {}", name, outputFile.getPath());
            return new ResourceLocation(AiModGenerator.MOD_ID, "textures/" + type + "/" + name);
        } catch (IOException e) {
            LOGGER.error("Failed to generate default texture: {}", e.getMessage());
            return null;
        }
    }
}
