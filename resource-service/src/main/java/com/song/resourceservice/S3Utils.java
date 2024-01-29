package com.song.resourceservice;

import java.util.Map;

public class S3Utils {

    public static String constructS3Location(String bucketName, String region, String key) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }
    public static Map<String, String> deconstructS3Location(String location) {
        String bucketName = location.substring(location.indexOf("//"), location.indexOf(".s3"));
        String region = location.substring(location.indexOf("s3."), location.indexOf(".amazon"));
        String key = location.substring(location.indexOf(".com/"));
        return Map.of("bucketName", bucketName, "region", region, "key", key);
    }
}
