package com.example.aimodgen.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite that includes all major test categories.
 * This runs smoke tests, content generation tests, and integration tests.
 */
@DisplayName("Comprehensive Test Suite")
public class ComprehensiveTestSuite {

    @Test
    @Tag("smoke")
    @DisplayName("Basic Mod Loading")
    public void testModLoading() {
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.AiModGenerator");
        }, "Mod main class should be loadable");
    }

    @Test
    @Tag("smoke")
    @DisplayName("Core Classes Available")
    public void testCoreClasses() {
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.generation.ContentGenerator");
            Class.forName("com.example.aimodgen.generation.ContentRegistry");
        }, "Core mod classes should be loadable");
    }

    @Test
    @Tag("contentgen")
    @DisplayName("Content Generation Framework")
    public void testContentGeneration() {
        // Test that content generation classes are available
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.generation.GeneratedContent");
            Class.forName("com.example.aimodgen.generation.ContentType");
        }, "Content generation classes should be available");
    }

    @Test
    @Tag("integration")
    @DisplayName("AI Integration Components")
    public void testAIIntegration() {
        // Test that AI integration classes are available
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.ai.LLMService");
            Class.forName("com.example.aimodgen.ai.LLMServiceFactory");
        }, "AI integration classes should be available");
    }

    @Test
    @Tag("texture")
    @DisplayName("Texture Generation Components")
    public void testTextureGeneration() {
        // Test that texture generation classes are available
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.generation.TextureGenerator");
        }, "Texture generation classes should be available");
    }
}
