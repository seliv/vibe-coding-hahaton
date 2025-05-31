package com.vibe.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vibe.server.model.Chat;
import com.vibe.server.model.Message;
import com.vibe.server.model.Message.MessageType;
import com.vibe.server.model.User;
import com.vibe.server.repository.ChatRepository;
import com.vibe.server.repository.MessageRepository;
import com.vibe.server.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for message operations.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get messages for a chat.
     *
     * @param chatId the chat ID
     * @param pageable the pagination information
     * @return the page of messages
     * @throws IllegalArgumentException if the chat is not found
     */
    public Page<Message> getMessagesForChat(Long chatId, Pageable pageable) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        return messageRepository.findByChatOrderBySentAtDesc(chat, pageable);
    }

    /**
     * Send a text message to a chat.
     *
     * @param chatId the chat ID
     * @param senderId the sender ID
     * @param content the message content
     * @return the sent message
     * @throws IllegalArgumentException if the chat or sender is not found
     */
    @Transactional
    public Message sendTextMessage(Long chatId, Long senderId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));

        // Check if the sender is a participant in the chat
        if (!chat.getParticipants().contains(sender)) {
            throw new IllegalArgumentException("Sender is not a participant in the chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setType(MessageType.TEXT);
        message.setSentAt(LocalDateTime.now());
        message.setDelivered(false);
        message.setRead(false);

        return messageRepository.save(message);
    }

    /**
     * Send an image message to a chat.
     *
     * @param chatId the chat ID
     * @param senderId the sender ID
     * @param attachmentUrl the URL of the image attachment
     * @return the sent message
     * @throws IllegalArgumentException if the chat or sender is not found
     */
    @Transactional
    public Message sendImageMessage(Long chatId, Long senderId, String attachmentUrl) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));

        // Check if the sender is a participant in the chat
        if (!chat.getParticipants().contains(sender)) {
            throw new IllegalArgumentException("Sender is not a participant in the chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setType(MessageType.IMAGE);
        message.setAttachmentUrl(attachmentUrl);
        message.setSentAt(LocalDateTime.now());
        message.setDelivered(false);
        message.setRead(false);

        return messageRepository.save(message);
    }

    /**
     * Mark messages as delivered for a user.
     *
     * @param userId the user ID
     * @throws IllegalArgumentException if the user is not found
     */
    @Transactional
    public void markMessagesAsDelivered(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<Message> undeliveredMessages = messageRepository.findUndeliveredMessagesForUser(user);
        for (Message message : undeliveredMessages) {
            message.setDelivered(true);
            messageRepository.save(message);
        }
    }

    /**
     * Mark messages as read for a user in a chat.
     *
     * @param chatId the chat ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the chat or user is not found
     */
    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Check if the user is a participant in the chat
        if (!chat.getParticipants().contains(user)) {
            throw new IllegalArgumentException("User is not a participant in the chat");
        }

        Page<Message> messages = messageRepository.findByChatOrderBySentAtDesc(chat, Pageable.unpaged());
        for (Message message : messages) {
            if (!message.getSender().getId().equals(userId) && !message.isRead()) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    /**
     * Search for messages containing the given text.
     *
     * @param userId the user ID
     * @param query the search query
     * @param pageable the pagination information
     * @return the page of messages
     * @throws IllegalArgumentException if the user is not found
     */
    public Page<Message> searchMessages(Long userId, String query, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return messageRepository.searchMessages(query, user, pageable);
    }
}