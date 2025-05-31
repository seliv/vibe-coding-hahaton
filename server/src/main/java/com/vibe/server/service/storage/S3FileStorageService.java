package com.vibe.server.service.storage;

import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * S3 implementation of FileStorageService.
 */
public class S3FileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3FileStorageService.class);
    
    private final S3Client s3Client;
    private final String bucketName;
    
    public S3FileStorageService(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }
    
    @Override
    public String storeFile(String filename, String contentType, InputStream inputStream) {
        try {
            String key = StringUtils.cleanPath(filename);
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, -1));
            
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
        } catch (S3Exception e) {
            logger.error("Error storing file to S3", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    @Override
    public Optional<InputStream> getFile(String filename) {
        try {
            String key = StringUtils.cleanPath(filename);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            return Optional.of(s3Client.getObject(getObjectRequest));
        } catch (S3Exception e) {
            logger.error("Error getting file from S3", e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean deleteFile(String filename) {
        try {
            String key = StringUtils.cleanPath(filename);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (S3Exception e) {
            logger.error("Error deleting file from S3", e);
            return false;
        }
    }
}