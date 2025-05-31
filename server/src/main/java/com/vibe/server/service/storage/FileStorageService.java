package com.vibe.server.service.storage;

import java.io.InputStream;
import java.util.Optional;

/**
 * Interface for file storage operations.
 */
public interface FileStorageService {
    
    /**
     * Store a file.
     *
     * @param filename the name of the file
     * @param contentType the content type of the file
     * @param inputStream the input stream of the file
     * @return the URL of the stored file
     */
    String storeFile(String filename, String contentType, InputStream inputStream);
    
    /**
     * Get a file.
     *
     * @param filename the name of the file
     * @return the input stream of the file, or empty if the file doesn't exist
     */
    Optional<InputStream> getFile(String filename);
    
    /**
     * Delete a file.
     *
     * @param filename the name of the file
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String filename);
}