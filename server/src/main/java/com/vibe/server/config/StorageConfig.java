package com.vibe.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import com.vibe.server.service.storage.FileStorageService;
import com.vibe.server.service.storage.LocalFileStorageService;
import com.vibe.server.service.storage.S3FileStorageService;

/**
 * Storage configuration for file storage (S3/local).
 */
@Configuration
public class StorageConfig {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    @Value("${aws.s3.access-key}")
    private String accessKey;
    
    @Value("${aws.s3.secret-key}")
    private String secretKey;
    
    @Value("${local.storage.path}")
    private String localStoragePath;
    
    @Bean
    @Profile("prod")
    public FileStorageService s3FileStorageService() {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        
        return new S3FileStorageService(s3Client, bucketName);
    }
    
    @Bean
    @Profile("dev")
    public FileStorageService localFileStorageService() {
        return new LocalFileStorageService(localStoragePath);
    }
}