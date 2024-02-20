package com.song.resourceprocessor.service.impl;

import com.song.resourceprocessor.client.ResourceServiceClient;
import com.song.resourceprocessor.client.SongServiceClient;
import com.song.resourceprocessor.dto.SongDTO;
import com.song.resourceprocessor.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResourceProcessorServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ResourceProcessorServiceImpl.class);

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final ResourceUtils resourceUtils;

    @Bean
    public KStream<String, Map<String, Long>> processUploadedResource(StreamsBuilder streamsBuilder) {
        KStream<String, Map<String, Long>> kStream = streamsBuilder.stream("resourceUploaded");
        kStream
                .filter((key, value) -> value != null)
                .foreach((key, value) -> {
                    logger.info("Received uploaded resource for processing, key: {}, value: {}", key, value);
                    Long resourceId = value.get("resourceId");
                    ResponseEntity<Map<String, String>> data = resourceServiceClient.getResourceData(resourceId);
                    if (data != null) {
                        Map<String, String> body = data.getBody();
                        if (body != null) {
                            byte[] resourceData = body.get("resourceData").getBytes();
                            SongDTO songDTO = resourceUtils.parseMetadataAndConvertToSongDTO(resourceData);
                            songDTO.setResourceId(String.valueOf(resourceId));
                            logger.info("Received response from parseMetadataAndConvertToSongDTO, with the attributes {}", songDTO);
                            songServiceClient.createSong(songDTO);
                        }
                    }
                });
        return kStream;
    }
}
