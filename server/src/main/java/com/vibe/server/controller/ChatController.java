package com.vibe.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.vibe.server.model.Chat;
import com.vibe.server.model.User;
import com.vibe.server.service.ChatService;
import com.vibe.server.service.UserService;

import java.util.List;
import java.util.Set;

/**
 * Controller for chat operations.
 */
@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    /**
     * Get all chats for the current user.
     *
     * @param userDetails the authenticated user details
     * @return the list of chats
     */
    @GetMapping
    public ResponseEntity<List<Chat>> getChats(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Chat> chats = chatService.getChatsByUserId(user.getId());
        return ResponseEntity.ok(chats);
    }

    /**
     * Get a chat by ID.
     *
     * @param chatId the chat ID
     * @return the chat
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Create a direct chat with a contact.
     *
     * @param userDetails the authenticated user details
     * @param contactId the contact ID
     * @return the created chat
     */
    @PostMapping("/direct/{contactId}")
    public ResponseEntity<Chat> createDirectChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long contactId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Chat chat = chatService.createDirectChat(user.getId(), contactId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Create a group chat.
     *
     * @param userDetails the authenticated user details
     * @param request the request body
     * @return the created chat
     */
    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateGroupChatRequest request) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Chat chat = chatService.createGroupChat(user.getId(), request.getName(), request.getParticipantIds());
        return ResponseEntity.ok(chat);
    }

    /**
     * Add a participant to a group chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID to add
     * @return the updated chat
     */
    @PostMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<Chat> addParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        Chat chat = chatService.addParticipant(chatId, userId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Remove a participant from a group chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID to remove
     * @return the updated chat
     */
    @DeleteMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<Chat> removeParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        Chat chat = chatService.removeParticipant(chatId, userId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Leave a group chat.
     *
     * @param userDetails the authenticated user details
     * @param chatId the chat ID
     * @return a success response
     */
    @PostMapping("/{chatId}/leave")
    public ResponseEntity<Void> leaveChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        chatService.leaveChat(chatId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a chat.
     *
     * @param userDetails the authenticated user details
     * @param chatId the chat ID
     * @return a success response
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        chatService.deleteChat(chatId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Request body for creating a group chat.
     */
    public static class CreateGroupChatRequest {
        private String name;
        private Set<Long> participantIds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Long> getParticipantIds() {
            return participantIds;
        }

        public void setParticipantIds(Set<Long> participantIds) {
            this.participantIds = participantIds;
        }
    }
}