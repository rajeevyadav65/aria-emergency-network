package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists AI chatbot conversation history per session/device.
 */
@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;   // deviceId or email

    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @Column(length = 5000)
    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum MessageRole {
        USER, ASSISTANT
    }
}
