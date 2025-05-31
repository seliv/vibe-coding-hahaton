package com.vibe.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vibe.server.model.Chat;
import com.vibe.server.model.Chat.ChatType;
import com.vibe.server.model.User;
import com.vibe.server.repository.ChatRepository;
import com.vibe.server.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Service for chat operations.
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get a chat by ID.
     *
     * @param chatId the chat ID
     * @return the chat
     * @throws IllegalArgumentException if the chat is not found
     */
    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
    }

    /**
     * Get all chats for a user.
     *
     * @param userId the user ID
     * @return the list of chats
     * @throws IllegalArgumentException if the user is not found
     */
    public List<Chat> getChatsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return chatRepository.findByParticipantsContaining(user);
    }

    /**
     * Create a direct chat between two users.
     *
     * @param userId the user ID
     * @param contactId the contact ID
     * @return the created chat
     * @throws IllegalArgumentException if the user or contact is not found
     */
    @Transactional
    public Chat createDirectChat(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));

        // Check if a direct chat already exists
        List<Chat> existingChats = chatRepository.findDirectChatBetweenUsers(user, contact);
        if (!existingChats.isEmpty()) {
            return existingChats.get(0);
        }

        // Create a new direct chat
        Chat chat = new Chat();
        chat.setName(contact.getUsername());
        chat.setType(ChatType.DIRECT);
        chat.setOwner(user);
        chat.addParticipant(user);
        chat.addParticipant(contact);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    /**
     * Create a group chat.
     *
     * @param userId the user ID
     * @param name the chat name
     * @param participantIds the IDs of the participants
     * @return the created chat
     * @throws IllegalArgumentException if the user or any participant is not found
     */
    @Transactional
    public Chat createGroupChat(Long userId, String name, Set<Long> participantIds) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Create a new group chat
        Chat chat = new Chat();
        chat.setName(name);
        chat.setType(ChatType.GROUP);
        chat.setOwner(owner);
        chat.addParticipant(owner);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());

        // Add participants
        for (Long participantId : participantIds) {
            if (participantId.equals(userId)) {
                continue; // Skip the owner
            }
            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
            chat.addParticipant(participant);
        }

        return chatRepository.save(chat);
    }

    /**
     * Add a participant to a group chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID to add
     * @return the updated chat
     * @throws IllegalArgumentException if the chat or user is not found
     * @throws IllegalStateException if the chat is not a group chat
     */
    @Transactional
    public Chat addParticipant(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (chat.getType() != ChatType.GROUP) {
            throw new IllegalStateException("Cannot add participant to a direct chat");
        }

        chat.addParticipant(user);
        chat.setUpdatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    /**
     * Remove a participant from a group chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID to remove
     * @return the updated chat
     * @throws IllegalArgumentException if the chat or user is not found
     * @throws IllegalStateException if the chat is not a group chat or the user is the owner
     */
    @Transactional
    public Chat removeParticipant(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (chat.getType() != ChatType.GROUP) {
            throw new IllegalStateException("Cannot remove participant from a direct chat");
        }

        if (chat.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("Cannot remove the owner from the chat");
        }

        chat.removeParticipant(user);
        chat.setUpdatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    /**
     * Leave a group chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the chat or user is not found
     * @throws IllegalStateException if the chat is not a group chat or the user is the owner
     */
    @Transactional
    public void leaveChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (chat.getType() != ChatType.GROUP) {
            throw new IllegalStateException("Cannot leave a direct chat");
        }

        if (chat.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("The owner cannot leave the chat");
        }

        chat.removeParticipant(user);
        chat.setUpdatedAt(LocalDateTime.now());

        chatRepository.save(chat);
    }

    /**
     * Delete a chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the chat or user is not found
     * @throws IllegalStateException if the user is not the owner of the chat
     */
    @Transactional
    public void deleteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!chat.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("Only the owner can delete the chat");
        }

        chatRepository.delete(chat);
    }
}