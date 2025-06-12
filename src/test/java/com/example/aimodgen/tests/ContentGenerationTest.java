package com.example.aimodgen.tests;

import com.example.aimodgen.generation.ContentRegistry;
import com.example.aimodgen.generation.GeneratedContent;
import com.example.aimodgen.generation.ContentType;
import com.google.gson.JsonObject;

/**
 * Unit tests for the content generation system
 * Note: Tests are currently simplified due to test framework setup
 */
public class ContentGenerationTest {

    /**
     * Simple test method - can be manually called for verification
     */    public static void testContentRegistryInit() {
        try {
            ContentRegistry.init();
            System.out.println("[PASS] Content registry initialization test passed");
        } catch (Exception e) {
            System.err.println("[FAIL] Content registry initialization test failed: " + e.getMessage());
        }
    }

    /**
     * Test generated content creation
     */
    public static void testGeneratedContentCreation() {
        try {
            JsonObject properties = new JsonObject();
            properties.addProperty("damage", 5);
            properties.addProperty("durability", 100);
            
            GeneratedContent content = new GeneratedContent(
                ContentType.ITEM,
                "test_sword",
                "Test Sword",
                properties,
                new byte[0]
            );
              if (content.getType() == ContentType.ITEM &&
                "test_sword".equals(content.getId()) &&
                "Test Sword".equals(content.getName())) {
                System.out.println("[PASS] Generated content creation test passed");
            } else {
                System.err.println("[FAIL] Generated content creation test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Generated content creation test failed: " + e.getMessage());
        }
    }

    /**
     * Test content registry operations
     */
    public static void testContentRegistryOperations() {
        try {
            JsonObject properties = new JsonObject();
            properties.addProperty("healAmount", 5);
            properties.addProperty("cooldown", 100);
            
            GeneratedContent content = new GeneratedContent(
                ContentType.ITEM,
                "test_wand",
                "Test Wand",
                properties,
                new byte[0]
            );
            
            // Add to registry
            ContentRegistry.register(content);
            
            // Verify it exists
            GeneratedContent retrieved = ContentRegistry.getContent("test_wand");            if (retrieved != null && 
                "Test Wand".equals(retrieved.getName()) &&
                retrieved.getType() == ContentType.ITEM) {
                System.out.println("[PASS] Content registry operations test passed");
            } else {
                System.err.println("[FAIL] Content registry operations test failed");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Content registry operations test failed: " + e.getMessage());
        }
    }

    /**
     * Run all tests
     */
    public static void runAllTests() {
        System.out.println("Running AI Mod Generator Tests...");
        testContentRegistryInit();
        testGeneratedContentCreation();
        testContentRegistryOperations();
        System.out.println("Tests completed.");
    }
}
