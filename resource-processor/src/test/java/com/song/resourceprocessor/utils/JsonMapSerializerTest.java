package com.song.resourceprocessor.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonMapSerializerTest {

    private JsonMapSerializer jsonMapSerializer;

    @BeforeEach
    void setUp() {
        jsonMapSerializer = new JsonMapSerializer();
    }

    @Test
    void serialize_ValidMap_ReturnsJsonBytes() {
        Map<String, Long> testData = new HashMap<>();
        testData.put("key1", 123L);
        testData.put("key2", 456L);

        byte[] serializedData = jsonMapSerializer.serialize("testTopic", testData);

        assertNotNull(serializedData);
        String jsonResult = new String(serializedData);
        assertTrue(jsonResult.contains("\"key1\":123"));
        assertTrue(jsonResult.contains("\"key2\":456"));
    }

    @Test
    void serialize_NullMap_ReturnsNull() {
        byte[] serializedData = jsonMapSerializer.serialize("testTopic", null);

        assertNull(serializedData);
    }
}
