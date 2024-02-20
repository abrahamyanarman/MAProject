package com.song.resourceservice.controller;

import com.song.resourceservice.entity.Resource;
import com.song.resourceservice.service.ResourceService;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Map<String, String>> createResource(@NotNull @RequestBody MultipartFile file) {
        try {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (StringUtils.isNotBlank(extension) && !extension.equalsIgnoreCase("mp3")) {
                logger.error("Invalid extension {} -  Supported file extension only mp3.", extension);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("error", String.format("ERROR-1101: %s files not supported.", extension)));
            }
            Long resourceId = resourceService.uploadResource(file);
            Map<String, String> response = Map.of("id", String.valueOf(resourceId));
            return ResponseEntity.ok(response);
        } catch (IOException ioe) {
            logger.error("{} occurred during the reading the file", IOException.class.getSimpleName(), ioe);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "ERROR-1001: Error during the reading the file."));
        } catch (Exception e) {
            logger.error("{} occurred", Exception.class.getSimpleName(), e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getResource(@PathVariable Long id) {
        Optional<Resource> resourceOptional = resourceService.getResourceById(id);
        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();
            return ResponseEntity.ok(Map.of("id", resource.getId(), "locaion", resource.getLocation()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/data/{id}")
    public ResponseEntity<Map<String, Object>> getResourceData(@PathVariable Long id) {
        byte[] data = resourceService.getResourceDataById(id);
        if (data != null) {
            return ResponseEntity.ok(Map.of("resourceData", data));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Long[]>> deleteResources(@RequestParam("id") Long[] ids) {
        resourceService.deleteResources(ids);
        Map<String, Long[]> response = Collections.singletonMap("ids", ids);
        return ResponseEntity.ok(response);
    }
}
