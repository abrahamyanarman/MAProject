package com.song.resourceservice.service;

import com.song.resourceservice.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ResourceService {
    Long uploadResource(MultipartFile file) throws Exception;

    Optional<Resource> getResourceById(Long id);
    byte[] getResourceDataById(Long id);

    void deleteResources(Long[] ids);
}
