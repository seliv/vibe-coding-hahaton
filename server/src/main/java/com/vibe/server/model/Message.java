package com.vibe.server.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Message entity.
 */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    @NotNull
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @NotNull
    private User sender;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String attachmentUrl;

    private LocalDateTime sentAt;

    private boolean delivered;

    private boolean read;

    // Default constructor
    public Message() {
        this.sentAt = LocalDateTime.now();
        this.delivered = false;
        this.read = false;
        this.type = MessageType.TEXT;
    }

    // Constructor with chat, sender, and content
    public Message(Chat chat, User sender, String content) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.delivered = false;
        this.read = false;
        this.type = MessageType.TEXT;
    }

    // Constructor with chat, sender, content, and type
    public Message(Chat chat, User sender, String content, MessageType type) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.sentAt = LocalDateTime.now();
        this.delivered = false;
        this.read = false;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    // Message type enum
    public enum MessageType {
        TEXT,
        IMAGE
    }
}