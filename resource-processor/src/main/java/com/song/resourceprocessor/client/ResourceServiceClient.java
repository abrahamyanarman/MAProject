package com.song.resourceprocessor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "resource-service", url = "http://resource-service:6082")
public interface ResourceServiceClient {
    @GetMapping(value = "/resources/data/{id}")
    ResponseEntity<Map<String, String>> getResourceData(@PathVariable("id") Long id);
}
