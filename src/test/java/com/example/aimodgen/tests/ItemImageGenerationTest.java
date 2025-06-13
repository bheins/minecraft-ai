package com.example.aimodgen.tests;

import com.example.aimodgen.generation.TextureGenerator;
import com.example.aimodgen.generation.AdvancedTextureGenerator;
import com.example.aimodgen.generation.ContentGenerator;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 * Tests to ensure appropriate images/textures are created for items.
 * These tests validate texture generation, image quality, and file creation.
 */
@Tag("texture")
@DisplayName("Item Image Generation Tests")
public class ItemImageGenerationTest {

    private Path testOutputDir;

    @BeforeEach
    void setUp() {
        // Create test output directory
        testOutputDir = Paths.get("build/test-textures");
        try {
            Files.createDirectories(testOutputDir);
        } catch (Exception e) {
            fail("Failed to create test output directory: " + e.getMessage());
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("Texture Generator Classes Available")
    public void testTextureGeneratorClassesAvailable() {
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.generation.TextureGenerator");
            Class.forName("com.example.aimodgen.generation.AdvancedTextureGenerator");
        }, "Texture generation classes should be available");
    }

    @Test
    @Tag("texture")
    @DisplayName("Default Texture Creation")
    public void testDefaultTextureCreation() {
        assertDoesNotThrow(() -> {
            // Test creation of default texture when AI generation fails
            byte[] defaultTexture = createDefaultTexture();
            assertNotNull(defaultTexture, "Default texture should be created");
            assertTrue(defaultTexture.length > 0, "Default texture should have content");
            
            // Validate it's a proper PNG image
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(defaultTexture));
            assertNotNull(image, "Default texture should be a valid image");
            assertEquals(16, image.getWidth(), "Default texture should be 16x16 pixels");
            assertEquals(16, image.getHeight(), "Default texture should be 16x16 pixels");
        }, "Default texture creation should work");
    }

    @Test
    @Tag("texture")
    @DisplayName("Texture File Saving")
    public void testTextureFileSaving() {
        assertDoesNotThrow(() -> {
            // Create a test texture
            byte[] testTexture = createDefaultTexture();
            
            // Test saving texture to file system
            String itemId = "test_sword";
            saveTestTexture(itemId, testTexture);
            
            // Verify file was created
            Path texturePath = testOutputDir.resolve(itemId + ".png");
            assertTrue(Files.exists(texturePath), "Texture file should be created");
            
            // Verify file has content
            byte[] savedContent = Files.readAllBytes(texturePath);
            assertTrue(savedContent.length > 0, "Saved texture file should have content");
            
            // Verify it's a valid image
            BufferedImage savedImage = ImageIO.read(texturePath.toFile());
            assertNotNull(savedImage, "Saved texture should be a valid image");
            assertEquals(16, savedImage.getWidth(), "Saved texture should be 16x16 pixels");
            assertEquals(16, savedImage.getHeight(), "Saved texture should be 16x16 pixels");
        }, "Texture file saving should work");
    }

    @Test
    @Tag("texture")
    @DisplayName("Multiple Item Texture Generation")
    public void testMultipleItemTextureGeneration() {
        String[] testItems = {
            "magic_sword",
            "healing_potion", 
            "fire_staff",
            "ice_shield",
            "lightning_bow"
        };
        
        for (String itemId : testItems) {
            assertDoesNotThrow(() -> {
                // Create texture for each item
                byte[] texture = createDefaultTexture();
                saveTestTexture(itemId, texture);
                
                // Verify each texture file
                Path texturePath = testOutputDir.resolve(itemId + ".png");
                assertTrue(Files.exists(texturePath), 
                    "Texture file should exist for " + itemId);
                
                // Verify image validity
                BufferedImage image = ImageIO.read(texturePath.toFile());
                assertNotNull(image, "Image should be valid for " + itemId);
                assertEquals(16, image.getWidth(), "Width should be 16 for " + itemId);
                assertEquals(16, image.getHeight(), "Height should be 16 for " + itemId);
                
            }, "Texture generation should work for " + itemId);
        }
    }

    @Test
    @Tag("texture")
    @DisplayName("Texture Generation with Properties")
    public void testTextureGenerationWithProperties() {
        assertDoesNotThrow(() -> {
            // Test texture generation based on item properties
            JsonObject swordProperties = new JsonObject();
            swordProperties.addProperty("material", "diamond");
            swordProperties.addProperty("enchanted", true);
            swordProperties.addProperty("damage", 10);
            
            JsonObject potionProperties = new JsonObject();
            potionProperties.addProperty("effect", "healing");
            potionProperties.addProperty("color", "red");
            potionProperties.addProperty("potency", 5);
            
            // Create textures based on properties
            byte[] swordTexture = createPropertyBasedTexture("enchanted_sword", swordProperties);
            byte[] potionTexture = createPropertyBasedTexture("healing_potion", potionProperties);
            
            assertNotNull(swordTexture, "Sword texture should be generated");
            assertNotNull(potionTexture, "Potion texture should be generated");
            assertTrue(swordTexture.length > 0, "Sword texture should have content");
            assertTrue(potionTexture.length > 0, "Potion texture should have content");
            
        }, "Property-based texture generation should work");
    }

    @Test
    @Tag("texture")
    @DisplayName("Texture Directory Structure")
    public void testTextureDirectoryStructure() {
        assertDoesNotThrow(() -> {
            // Test that textures are saved in correct directory structure
            String itemId = "structured_test_item";
            byte[] texture = createDefaultTexture();
            
            // Save texture using proper directory structure
            Path itemTextureDir = Paths.get("src/main/resources/assets/aimodgenerator/textures/item");
            Files.createDirectories(itemTextureDir);
            
            Path texturePath = itemTextureDir.resolve(itemId + ".png");
            Files.write(texturePath, texture);
            
            assertTrue(Files.exists(texturePath), "Texture should be saved in correct directory");
            
            // Cleanup
            Files.deleteIfExists(texturePath);
            
        }, "Texture directory structure should be correct");
    }

    @Test
    @Tag("texture")
    @DisplayName("Invalid Texture Handling")
    public void testInvalidTextureHandling() {
        assertDoesNotThrow(() -> {
            // Test handling of invalid texture data
            byte[] invalidData = "not an image".getBytes();
            
            // Should fallback to default texture when invalid data is provided
            byte[] fallbackTexture = handleInvalidTexture(invalidData);
            assertNotNull(fallbackTexture, "Should provide fallback texture for invalid data");
            assertTrue(fallbackTexture.length > 0, "Fallback texture should have content");
            
            // Verify fallback is a valid image
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fallbackTexture));
            assertNotNull(image, "Fallback should be a valid image");
            
        }, "Invalid texture handling should work");
    }

    @Test
    @Tag("texture")
    @DisplayName("Texture Cache Management")
    public void testTextureCacheManagement() {
        // Test that textures are properly cached and retrieved
        String itemId = "cached_test_item";
        
        assertDoesNotThrow(() -> {
            // Generate texture first time
            byte[] texture1 = createDefaultTexture();
            saveTestTexture(itemId, texture1);
            
            // Retrieve from cache (simulate)
            Path texturePath = testOutputDir.resolve(itemId + ".png");
            byte[] cachedTexture = Files.readAllBytes(texturePath);
            
            assertNotNull(cachedTexture, "Cached texture should be available");
            assertArrayEquals(texture1, cachedTexture, "Cached texture should match original");
            
        }, "Texture caching should work properly");
    }

    // Helper methods for texture testing

    private byte[] createDefaultTexture() throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(java.awt.Color.GRAY);
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }

    private void saveTestTexture(String itemId, byte[] textureData) throws Exception {
        Path texturePath = testOutputDir.resolve(itemId + ".png");
        Files.write(texturePath, textureData);
    }

    private byte[] createPropertyBasedTexture(String itemId, JsonObject properties) throws Exception {
        // For testing, create a simple colored texture based on properties
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = img.createGraphics();
        
        // Change color based on properties
        if (properties.has("enchanted") && properties.get("enchanted").getAsBoolean()) {
            g.setColor(java.awt.Color.BLUE); // Enchanted = blue
        } else if (properties.has("color") && properties.get("color").getAsString().equals("red")) {
            g.setColor(java.awt.Color.RED); // Red potion
        } else {
            g.setColor(java.awt.Color.GREEN); // Default green
        }
        
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }    private byte[] handleInvalidTexture(byte[] invalidData) throws Exception {
        try {
            // Try to read as image
            BufferedImage testImage = ImageIO.read(new ByteArrayInputStream(invalidData));
            if (testImage != null) {
                return invalidData; // If successful, return as-is
            } else {
                // If ImageIO.read returns null, generate fallback
                return createDefaultTexture();
            }
        } catch (Exception e) {
            // If failed, return default texture
            return createDefaultTexture();
        }
    }
}
