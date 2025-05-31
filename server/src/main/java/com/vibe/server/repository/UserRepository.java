package com.vibe.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vibe.server.model.User;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username.
     *
     * @param username the username
     * @return the user, or empty if not found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if a user exists by username.
     *
     * @param username the username
     * @return true if the user exists, false otherwise
     */
    boolean existsByUsername(String username);
}