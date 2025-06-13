package com.example.aimodgen.tests;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.ai.LLMService;
import com.example.aimodgen.generation.ContentRegistry;

/**
 * Basic smoke tests to verify mod is loading correctly.
 * These tests do minimal validation of critical components.
 */
public class SmokeTest {

    public static void main(String[] args) {
        System.out.println("\n=== Running Smoke Tests ===");
        
        testModLoading();
        testBasicComponents();
        testAIServices();
        
        // Run a subset of enhanced feature tests
        System.out.println("\n=== Running Critical Enhanced Feature Tests ===");
        testCriticalEnhancedFeatures();
        
        System.out.println("\n=== Smoke Tests completed ===");
        System.out.println("\nFor full testing, please run ComprehensiveTestSuite");
    }

    private static void testModLoading() {
        try {
            // Test that critical mod classes can be loaded
            Class<?> mainClass = Class.forName("com.example.aimodgen.AiModGenerator");
            System.out.println("[PASS] Main mod class loaded: " + mainClass.getSimpleName());
            
            // Verify some essential classes
            Class.forName("com.example.aimodgen.commands.AICommands");
            Class.forName("com.example.aimodgen.commands.EnhancedAICommands");
            Class.forName("com.example.aimodgen.generation.ContentGenerator");
            
            System.out.println("[PASS] Essential mod classes loaded successfully");
        } catch (Exception e) {
            System.err.println("[FAIL] Mod loading test failed: " + e.getMessage());
        }
    }

    private static void testBasicComponents() {
        try {
            // Test content registry initialization
            ContentRegistry.init();
            System.out.println("[PASS] Content registry initialized");
            
            // Test mod instance
            AiModGenerator instance = AiModGenerator.getInstance();
            if (instance != null) {
                System.out.println("[PASS] AiModGenerator instance created");
            } else {
                System.err.println("[FAIL] AiModGenerator instance is null");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Basic components test failed: " + e.getMessage());
        }
    }    private static void testAIServices() {
        try {
            // Test LLM services
            AiModGenerator instance = AiModGenerator.getInstance();
            LLMService llmService = instance.getLlmService();
            
            if (llmService != null) {
                System.out.println("[PASS] LLM Service created successfully");
                System.out.println("[INFO] Current LLM type: " + llmService.getClass().getSimpleName());
            } else {
                System.err.println("[FAIL] LLM Service is null");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] AI Services test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test critical enhanced AI features
     */
    private static void testCriticalEnhancedFeatures() {
        try {
            System.out.println("Testing critical enhanced AI features...");
            
            // Run only the texture generation options test from the EnhancedTestSuite
            // This avoids running all tests but still validates the core enhanced functionality
            System.out.println("Testing AdvancedTextureGenerator options:");
            com.example.aimodgen.tests.enhanced.AdvancedTextureGeneratorTestNew.testTextureGenerationOptions();
            
            System.out.println("[PASS] Critical enhanced features test");
        } catch (Exception e) {
            System.err.println("[FAIL] Enhanced features test failed: " + e.getMessage());
        }
    }
}