package com.vibe.server.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Provider for JWT key.
 * This class ensures that the same key is used for both token generation and validation.
 */
@Component
public class JwtKeyProvider {

    private final SecretKey jwtKey;

    public JwtKeyProvider(@Value("${jwt.secret}") String jwtSecret) {
        // Use the configured secret from application.properties
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
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