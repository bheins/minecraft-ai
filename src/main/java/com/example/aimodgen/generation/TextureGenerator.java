package com.example.aimodgen.generation;

import com.example.aimodgen.AiModGenerator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static ResourceLocation generateTexture(String name, String description, String type) {
        try {
            // Generate texture using AI
            String aiResponse = AiModGenerator.getInstance().getLlmService().generateTexture(description);
            if (aiResponse == null || aiResponse.isEmpty()) {
                LOGGER.warn("AI failed to generate texture for {}, using default", name);
                return generateDefaultTexture(name, type);
            }

            // Extract base64 from AI response
            String base64Image = extractBase64(aiResponse);
            if (base64Image == null) {
                LOGGER.warn("Could not extract valid base64 from AI response for {}, using default", name);
                return generateDefaultTexture(name, type);
            }

            // Convert base64 to image
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            
            if (image == null) {
                LOGGER.warn("Could not decode image data for {}, using default", name);
                return generateDefaultTexture(name, type);
            }

            // Ensure resources directory exists
            String directory = "src/main/resources/assets/" + AiModGenerator.MOD_ID + "/textures/" + type;
            Path dirPath = Paths.get(directory);
            Files.createDirectories(dirPath);

            // Save image
            File outputFile = new File(dirPath.toFile(), name + ".png");
            ImageIO.write(image, "PNG", outputFile);

            LOGGER.info("Generated texture for {} at {}", name, outputFile.getPath());
            return new ResourceLocation(AiModGenerator.MOD_ID, "textures/" + type + "/" + name);
        } catch (Exception e) {
            LOGGER.error("Failed to generate texture for {}: {}", name, e.getMessage());
            return generateDefaultTexture(name, type);
        }
    }
    
    /**
     * Extract base64 image data from various AI response formats
     */
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
    private static ResourceLocation generateDefaultTexture(String name, String type) {
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
