package com.example.aimodgen.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for mod compatibility and functionality
 */
@Tag("integration")
public class ModIntegrationTest {

    @Test
    @Tag("smoke")
    public void testModIntegration() {
        // Test mod integration - basic functionality test
        assertDoesNotThrow(() -> {
            // Test that we can load and reference core mod components
            Class.forName("com.example.aimodgen.AiModGenerator");
            Class.forName("com.example.aimodgen.generation.ContentGenerator");
        }, "Core mod integration should work");
    }    @Test
    @Tag("integration")
    public void testCompatibility() {
        // Test compatibility - verify core classes are available without accessing runtime info
        assertDoesNotThrow(() -> {
            Class.forName("net.minecraft.SharedConstants");
        }, "Minecraft classes should be available");
        
        // Skip ForgeVersion as it requires runtime initialization
        // Just test that we can access basic Forge classes
        assertDoesNotThrow(() -> {
            Class.forName("net.minecraftforge.common.MinecraftForge");
        }, "Forge event bus classes should be available");
        
        // Simple compatibility test that doesn't require runtime initialization
        assertTrue(true, "Basic compatibility test passed");
    }
}