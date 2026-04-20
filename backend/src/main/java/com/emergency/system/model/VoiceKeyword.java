package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Stores a user's secret voice trigger keyword.
 * When spoken, silently triggers an emergency alert.
 * Keyword is stored hashed (BCrypt) — never in plaintext.
 */
@Entity
@Table(name = "voice_keywords")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VoiceKeyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * BCrypt hash of the keyword — never store plaintext.
     * On the device, comparison is done locally; the hash
     * is only used for server-side verification if needed.
     */
    @Column(nullable = false)
    private String keywordHash;

    /** Hint displayed to user on the settings screen (e.g., "starts with H") */
    private String hint;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastTriggeredAt;
    private int triggerCount;
}
