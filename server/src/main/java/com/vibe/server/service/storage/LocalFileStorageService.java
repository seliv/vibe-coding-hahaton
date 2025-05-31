package com.vibe.server.service.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Local file system implementation of FileStorageService.
 */
public class LocalFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);
    
    private final String storageLocation;
    
    public LocalFileStorageService(String storageLocation) {
        this.storageLocation = storageLocation;
        init();
    }
    
    private void init() {
        try {
            Path storagePath = Paths.get(storageLocation);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                logger.info("Created storage directory: {}", storageLocation);
            }
        } catch (IOException e) {
            logger.error("Could not initialize storage location", e);
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }
    
    @Override
    public String storeFile(String filename, String contentType, InputStream inputStream) {
        try {
            String cleanFilename = StringUtils.cleanPath(filename);
            Path targetPath = Paths.get(storageLocation).resolve(cleanFilename);
            
            try (FileOutputStream outputStream = new FileOutputStream(targetPath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            return targetPath.toUri().toString();
        } catch (IOException e) {
            logger.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    @Override
    public Optional<InputStream> getFile(String filename) {
        try {
            String cleanFilename = StringUtils.cleanPath(filename);
            Path filePath = Paths.get(storageLocation).resolve(cleanFilename);
            File file = filePath.toFile();
            
            if (!file.exists()) {
                return Optional.empty();
            }
            
            return Optional.of(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean deleteFile(String filename) {
        try {
            String cleanFilename = StringUtils.cleanPath(filename);
            Path filePath = Paths.get(storageLocation).resolve(cleanFilename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Error deleting file", e);
            return false;
        }
    }
}