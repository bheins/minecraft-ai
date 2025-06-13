package com.example.aimodgen.tests;

import com.example.aimodgen.tests.enhanced.EnhancedTestSuite;

/**
 * Comprehensive test suite that runs all meaningful tests for the AI Mod Generator.
 * This avoids running useless simple tests and focuses on testing actual functionality.
 */
public class ComprehensiveTestSuite {

    public static void main(String[] args) {
        System.out.println("\n========================================================");
        System.out.println("==== AI Mod Generator - Comprehensive Test Suite v2.0 ====");
        System.out.println("========================================================\n");

        try {
            // Run content generation tests
            System.out.println("\n=== 1. Core Content Generation Tests ===\n");
            runContentGenerationTests();
            
            // Run behavior tests
            System.out.println("\n=== 2. AI Item Behavior Tests ===\n");
            runItemBehaviorTests();
            
            // Run enhanced AI texture generation tests
            System.out.println("\n=== 3. Enhanced AI Texture Generation Tests ===\n");
            runEnhancedTextureFunctionality();
            
            System.out.println("\n========================================================");
            System.out.println("====== Comprehensive Test Suite Execution Complete ======");
            System.out.println("========================================================\n");
            
        } catch (Exception e) {
            System.err.println("\n[CRITICAL ERROR] Test suite execution failed:");
            e.printStackTrace();
            System.err.println("\nTest suite terminated abnormally.\n");
        }
    }

    /**
     * Run content generation tests
     */
    private static void runContentGenerationTests() {
        try {
            System.out.println("Running content generation core tests...");
            
            // Test content registry initialization
            ContentGenerationTest.testContentRegistryInit();
            
            // Test content creation
            ContentGenerationTest.testGeneratedContentCreation();
            
            System.out.println("[COMPLETE] Content generation tests finished");
        } catch (Exception e) {
            System.err.println("[FAIL] Content generation tests failed: " + e.getMessage());
        }
    }
    
    /**
     * Run AI item behavior tests
     */    private static void runItemBehaviorTests() {
        try {
            System.out.println("Running AI item behavior tests...");
            
            // Call the main method which runs all the tests
            System.out.println("Executing AI Item Behavior Tests:");
            AIItemBehaviorTest.main(null);
            
            System.out.println("[COMPLETE] AI item behavior tests finished");
        } catch (Exception e) {
            System.err.println("[FAIL] AI item behavior tests failed: " + e.getMessage());
        }
    }
    
    /**
     * Run enhanced texture generation tests
     */
    private static void runEnhancedTextureFunctionality() {
        try {
            System.out.println("Running enhanced texture generation tests...");
            
            // Run the enhanced test suite
            EnhancedTestSuite.runAllEnhancedTests();
            
            // Run integration tests
            System.out.println("\n=== Integration Tests ===\n");
            EnhancedTestSuite.runIntegrationTests();
            
            System.out.println("[COMPLETE] Enhanced texture generation tests finished");
        } catch (Exception e) {
            System.err.println("[FAIL] Enhanced texture generation tests failed: " + e.getMessage());
        }
    }
}
