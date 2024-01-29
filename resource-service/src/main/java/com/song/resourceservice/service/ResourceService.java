package com.song.resourceservice.service;

import com.song.resourceservice.entity.Resource;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface ResourceService {
    Long uploadResource(MultipartFile file) throws Exception;

    Optional<Resource> getResourceById(Long id);

    void deleteResources(Long[] ids);
}
