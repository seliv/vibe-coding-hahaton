package com.vibe.server.service.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vibe.server.model.User;
import com.vibe.server.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service for authentication operations.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user.
     *
     * @param username the username
     * @param password the password
     * @return the registered user
     * @throws IllegalArgumentException if the username is already taken
     */
    @Transactional
    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param username the username
     * @param password the password
     * @return the JWT token
     * @throws BadCredentialsException if the credentials are invalid
     */
    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        return generateToken(user);
    }
    
    /**
     * Generate a JWT token for a user.
     *
     * @param user the user
     * @return the JWT token
     */
    private String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }
}