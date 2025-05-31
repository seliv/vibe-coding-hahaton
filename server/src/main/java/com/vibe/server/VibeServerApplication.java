package com.vibe.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * Main application class for the Vibe Server.
 */
@SpringBootApplication
@EnableJms
public class VibeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibeServerApplication.class, args);
    }
}