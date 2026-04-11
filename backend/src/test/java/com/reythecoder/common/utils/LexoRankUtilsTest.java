package com.reythecoder.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexoRankUtilsTest {

    private static final String CHARSET = "0123456789abcdefghijklmnopqrstuvwxyz";

    @Test
    void testInitialRank() {
        // Test index 0
        assertEquals("a0", LexoRankUtils.initialRank(0));
        // Test index 1
        assertEquals("b0", LexoRankUtils.initialRank(1));
        // Test index 10
        assertEquals("k0", LexoRankUtils.initialRank(10));
        // Test index 25
        assertEquals("z0", LexoRankUtils.initialRank(25));
    }

    @Test
    void testInitialRankBoundaryValues() {
        // Test boundary value 0
        assertEquals("a0", LexoRankUtils.initialRank(0));
        // Test boundary value 25 (last valid index)
        assertEquals("z0", LexoRankUtils.initialRank(25));
    }

    @Test
    void testInitialRankInvalidIndex() {
        // Test index >= 26
        assertThrows(IllegalArgumentException.class, () -> LexoRankUtils.initialRank(26));
        // Test negative index
        assertThrows(IllegalArgumentException.class, () -> LexoRankUtils.initialRank(-1));
    }

    @Test
    void testBetween() {
        // Test basic between calculation
        String result = LexoRankUtils.between("a0", "c0");
        assertNotNull(result);
        assertTrue(result.compareTo("a0") > 0);
        assertTrue(result.compareTo("c0") < 0);

        // Test between with same prefix
        String result2 = LexoRankUtils.between("a0", "a1");
        assertNotNull(result2);
        assertTrue(result2.compareTo("a0") > 0);
        assertTrue(result2.compareTo("a1") < 0);
    }

    @Test
    void testBetweenWithNullLower() {
        // When lower is null, should generate a value before upper
        String result = LexoRankUtils.between(null, "b0");
        assertNotNull(result);
        assertTrue(result.compareTo("b0") < 0);
    }

    @Test
    void testBetweenWithNullUpper() {
        // When upper is null, should generate a value after lower
        String result = LexoRankUtils.between("a0", null);
        assertNotNull(result);
        assertTrue(result.compareTo("a0") > 0);
    }

    @Test
    void testBetweenWithBothNull() {
        // When both are null, should generate initial rank
        String result = LexoRankUtils.between(null, null);
        assertEquals("a0", result);
    }

    @Test
    void testBetweenInvalidRanks() {
        // When lower >= upper, should throw exception
        assertThrows(IllegalArgumentException.class, () -> LexoRankUtils.between("c0", "a0"));
        assertThrows(IllegalArgumentException.class, () -> LexoRankUtils.between("a0", "a0"));
    }

    @Test
    void testBefore() {
        // Test before a normal rank
        String result = LexoRankUtils.before("b0");
        assertNotNull(result);
        assertTrue(result.compareTo("b0") < 0);

        // Test before with minimum rank
        String result2 = LexoRankUtils.before("a0");
        assertNotNull(result2);
        assertTrue(result2.compareTo("a0") < 0);
    }

    @Test
    void testBeforeWithNull() {
        // When current is null, should generate initial rank
        String result = LexoRankUtils.before(null);
        assertEquals("a0", result);
    }

    @Test
    void testAfter() {
        // Test after a normal rank
        String result = LexoRankUtils.after("a0");
        assertNotNull(result);
        assertTrue(result.compareTo("a0") > 0);

        // Test after with a higher rank
        String result2 = LexoRankUtils.after("y0");
        assertNotNull(result2);
        assertTrue(result2.compareTo("y0") > 0);
    }

    @Test
    void testAfterWithNull() {
        // When current is null, should generate initial rank
        String result = LexoRankUtils.after(null);
        assertEquals("a0", result);
    }

    @Test
    void testIsValidRank() {
        // Valid ranks
        assertTrue(LexoRankUtils.isValidRank("a0"));
        assertTrue(LexoRankUtils.isValidRank("z0"));
        assertTrue(LexoRankUtils.isValidRank("a00"));
        assertTrue(LexoRankUtils.isValidRank("abc123"));

        // Invalid ranks
        assertFalse(LexoRankUtils.isValidRank(null));
        assertFalse(LexoRankUtils.isValidRank(""));
        assertFalse(LexoRankUtils.isValidRank("A0")); // uppercase not allowed
        assertFalse(LexoRankUtils.isValidRank("0"));  // too short
        assertFalse(LexoRankUtils.isValidRank("a"));  // too short
        assertFalse(LexoRankUtils.isValidRank("a!0")); // invalid character
    }
}
