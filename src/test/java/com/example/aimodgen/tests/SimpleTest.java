package com.example.aimodgen.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal smoke test to verify the test framework is working
 */
public class SimpleTest {

    @Test
    public void testBasicAssertion() {
        assertTrue(true, "This test should always pass");
    }

    @Test
    public void testStringEquality() {
        assertEquals("hello", "hello", "Strings should be equal");
    }

    @Test
    public void testNumericEquality() {
        assertEquals(2 + 2, 4, "Math should work");
    }
}
