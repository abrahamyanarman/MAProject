package com.song.resourceprocessor.utils;

import com.song.resourceprocessor.utils.JsonMapDeserializer;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonMapDeserializerTest {

    private JsonMapDeserializer jsonMapDeserializer;

    @BeforeEach
    void setUp() {
        jsonMapDeserializer = new JsonMapDeserializer();
    }

    @Test
    void deserialize_ValidJson_ReturnsMap() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Long> originalMap = Map.of("key1", 123L, "key2", 456L);
        byte[] jsonData = objectMapper.writeValueAsBytes(originalMap);

        Map<String, Long> deserializedMap = jsonMapDeserializer.deserialize("testTopic", jsonData);

        assertNotNull(deserializedMap);
        assertEquals(2, deserializedMap.size());
        assertEquals(123L, deserializedMap.get("key1"));
        assertEquals(456L, deserializedMap.get("key2"));
    }

    @Test
    void deserialize_NullData_ReturnsNull() {
        Map<String, Long> deserializedMap = jsonMapDeserializer.deserialize("testTopic", null);

        assertNull(deserializedMap);
    }

    @Test
    void deserialize_InvalidJson_ThrowsSerializationException() {
        byte[] invalidJsonData = "invalid json".getBytes();

        assertThrows(SerializationException.class, () -> jsonMapDeserializer.deserialize("testTopic", invalidJsonData));
    }
}
