package com.vibe.server.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Provider for JWT key.
 * This class ensures that the same key is used for both token generation and validation.
 */
@Component
public class JwtKeyProvider {

    private final SecretKey jwtKey;

    public JwtKeyProvider() {
        // Generate a secure key for HS512 algorithm
        this.jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Get the JWT key.
     *
     * @return the JWT key
     */
    public SecretKey getJwtKey() {
        return jwtKey;
    }
}