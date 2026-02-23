package com.reythecoder.organization.util;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UuidV7Test {

    @Test
    void testUUIDv7Generation() {
        // When
        UUID uuid = UUIDv7.randomUUID();

        // Then
        assertNotNull(uuid, "UUID should not be null");
        assertEquals(7, uuid.version(), "UUID version should be 7 for UUIDv7");
        System.out.println("Generated UUID v7: " + uuid);
    }
}