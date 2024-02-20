package com.song.resourceprocessor.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonMapDeserializer implements Deserializer<Map<String, Long>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Long> deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, new TypeReference<Map<String, Long>>() {});
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}