package com.example.aimodgen.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic smoke tests to verify mod is loading correctly
 */
@Tag("smoke")
public class SmokeTest {

    @Test
    public void testModLoading() {
        // Basic test that the mod classes can be loaded
        assertDoesNotThrow(() -> {
            Class.forName("com.example.aimodgen.AiModGenerator");
        }, "Mod main class should be loadable");
    }

    @Test
    public void testBasicFunctionality() {
        // Basic functionality test - just verify we can instantiate key classes
        assertDoesNotThrow(() -> {
            // Test that key classes can be referenced
            Class.forName("com.example.aimodgen.generation.ContentGenerator");
            Class.forName("com.example.aimodgen.generation.ContentRegistry");
        }, "Core mod classes should be loadable");
    }
}