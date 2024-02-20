package com.song.resourceprocessor.client;

import com.song.resourceprocessor.dto.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "song-service", url = "http://song-service:6081")
public interface SongServiceClient {
    @PostMapping(value = "/songs")
    ResponseEntity<Map<String, Long>> createSong(@RequestBody SongDTO song);
}
