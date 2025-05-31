package com.vibe.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.vibe.server.model.Message;
import com.vibe.server.model.User;
import com.vibe.server.service.MessageService;
import com.vibe.server.service.UserService;
import com.vibe.server.service.storage.FileStorageService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Controller for message operations.
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public MessageController(MessageService messageService, UserService userService, FileStorageService fileStorageService) {
        this.messageService = messageService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Get messages for a chat.
     *
     * @param chatId the chat ID
     * @param pageable the pagination information
     * @return the page of messages
     */
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Page<Message>> getMessagesForChat(
            @PathVariable Long chatId,
            @PageableDefault(size = 20, sort = "sentAt") Pageable pageable) {
        Page<Message> messages = messageService.getMessagesForChat(chatId, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Send a text message to a chat.
     *
     * @param userDetails the authenticated user details
     * @param chatId the chat ID
     * @param request the request body
     * @return the sent message
     */
    @PostMapping("/chat/{chatId}/text")
    public ResponseEntity<Message> sendTextMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatId,
            @RequestBody TextMessageRequest request) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Message message = messageService.sendTextMessage(chatId, user.getId(), request.getContent());
        return ResponseEntity.ok(message);
    }

    /**
     * Send an image message to a chat.
     *
     * @param userDetails the authenticated user details
     * @param chatId the chat ID
     * @param file the image file
     * @return the sent message
     * @throws IOException if the file cannot be read
     */
    @PostMapping("/chat/{chatId}/image")
    public ResponseEntity<Message> sendImageMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getUserByUsername(userDetails.getUsername());
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        try (InputStream inputStream = file.getInputStream()) {
            String attachmentUrl = fileStorageService.storeFile(filename, contentType, inputStream);
            Message message = messageService.sendImageMessage(chatId, user.getId(), attachmentUrl);
            return ResponseEntity.ok(message);
        }
    }

    /**
     * Mark messages as delivered for the current user.
     *
     * @param userDetails the authenticated user details
     * @return a success response
     */
    @PostMapping("/delivered")
    public ResponseEntity<Void> markMessagesAsDelivered(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        messageService.markMessagesAsDelivered(user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Mark messages as read for the current user in a chat.
     *
     * @param userDetails the authenticated user details
     * @param chatId the chat ID
     * @return a success response
     */
    @PostMapping("/chat/{chatId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        messageService.markMessagesAsRead(chatId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Search for messages containing the given text.
     *
     * @param userDetails the authenticated user details
     * @param query the search query
     * @param pageable the pagination information
     * @return the page of messages
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Message>> searchMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Page<Message> messages = messageService.searchMessages(user.getId(), query, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Request body for sending a text message.
     */
    public static class TextMessageRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
