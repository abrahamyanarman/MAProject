package com.song.resourceservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.song.resourceservice.S3Utils;
import com.song.resourceservice.entity.Resource;
import com.song.resourceservice.repository.ResourceRepository;
import com.song.resourceservice.service.ResourceService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final static String BUCKET_NAME = "resources";
    private final ResourceRepository resourceRepository;
    private AmazonS3 amazonS3;
    @Autowired
    private KafkaTemplate<String, Map<String, Long>> kafkaTemplate;
    @Value("${cloud.aws.region.static}")
    private String region;

    public ResourceServiceImpl(ResourceRepository resourceRepository, AmazonS3 amazonS3) {
        this.resourceRepository = resourceRepository;
        this.amazonS3 = amazonS3;
    }
    @PostConstruct
    private void init() {
        try {
            if (!amazonS3.doesBucketExistV2(BUCKET_NAME)) {
                amazonS3.createBucket(BUCKET_NAME);
            }
        } catch (AmazonS3Exception e) {
            logger.error("Error while creating bucket with the name: {}, error: {}", BUCKET_NAME, e.getMessage());
        }
    }

    @Override
    public Long uploadResource(MultipartFile file) throws Exception {
        logger.info("{}: Uploading the file: {}", ResourceServiceImpl.class.getSimpleName(), file.getName());

        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            String key = file.getOriginalFilename();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getBytes().length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, objectMetadata);
            amazonS3.putObject(putObjectRequest);

            Resource resource = new Resource();
            resource.setLocation(S3Utils.constructS3Location(BUCKET_NAME, region, key));
            resource = resourceRepository.save(resource);

            if (resource.getId() != null) {
                logger.info("{}: Resource with id: {}, location: {} saved", ResourceServiceImpl.class.getSimpleName(), resource.getId(), resource.getLocation());
                kafkaTemplate.send("resourceUploaded", Map.of("resourceId", resource.getId()));
                return resource.getId();
            } else {
                logger.info("{}: Something went wrong saving resource with id: {}", ResourceServiceImpl.class.getSimpleName(), resource.getId());
                resourceRepository.delete(resource);
                amazonS3.deleteObject(BUCKET_NAME, key);
            }
        }
        throw new Exception("ERROR-1004: Something went wrong, file not saved");
    }


    @Override
    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public byte[] getResourceDataById(Long id) {
        Optional<Resource> resourceOptional = getResourceById(id);
        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();
            Map<String, String> props = S3Utils.deconstructS3Location(resource.getLocation());
            GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, props.get("key"));
            S3Object object = amazonS3.getObject(getObjectRequest);
            try {
                return object.getObjectContent().readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO add error handling
            }

        }
        return new byte[]{}; // TODO need refactoring
    }

    @Override
    public void deleteResources(Long[] ids) {
        for (Long id : ids) {
            resourceRepository.deleteById(id);
        }
    }
}
