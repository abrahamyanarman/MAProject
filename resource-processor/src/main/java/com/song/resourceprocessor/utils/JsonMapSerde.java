package com.song.resourceprocessor.utils;

import org.apache.kafka.common.serialization.Serdes;

import java.util.Map;

public class JsonMapSerde extends Serdes.WrapperSerde<Map<String, Long>> {
    public JsonMapSerde() {
        super(new JsonMapSerializer(), new JsonMapDeserializer());
    }
}
