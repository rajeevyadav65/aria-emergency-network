package com.emergency.system.service;

import com.emergency.system.model.User;
import com.emergency.system.model.VoiceKeyword;
import com.emergency.system.repository.VoiceKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manages the secret voice trigger keyword.
 *
 * SECURITY MODEL:
 * - Keyword is stored BCrypt-hashed, never in plaintext
 * - Actual speech recognition runs ON-DEVICE (Android SpeechRecognizer)
 * - Server only stores the hash for verification if needed
 * - The mobile app compares the recognized text against its local hash
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceKeywordService {

    private final VoiceKeywordRepository keywordRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register or update the user's secret keyword.
     * @param keyword the plaintext keyword (immediately hashed, never stored plaintext)
     */
    public Map<String, Object> setKeyword(User user, String keyword, String hint) {
        if (keyword == null || keyword.trim().length() < 4) {
            throw new IllegalArgumentException("Keyword must be at least 4 characters");
        }

        String hash = passwordEncoder.encode(keyword.trim().toLowerCase());

        VoiceKeyword vk = keywordRepo.findByUser(user)
                .orElse(VoiceKeyword.builder().user(user).build());
        vk.setKeywordHash(hash);
        vk.setHint(hint);
        vk.setActive(true);
        keywordRepo.save(vk);

        log.info("[VOICE] Keyword set/updated for user {}", user.getId());

        // FIX APPLIED HERE: Added <String, Object>
        return Map.<String, Object>of(
                "message", "Voice keyword set successfully",
                "hint", hint != null ? hint : "",
                "active", true
        );
    }

    /**
     * Verifies a spoken keyword against the stored hash.
     * Called when the device cannot verify locally (fallback).
     */
    public boolean verifyKeyword(User user, String spokenText) {
        return keywordRepo.findByUserAndActive(user, true)
                .map(vk -> {
                    boolean match = passwordEncoder.matches(
                            spokenText.trim().toLowerCase(), vk.getKeywordHash());
                    if (match) {
                        vk.setLastTriggeredAt(LocalDateTime.now());
                        vk.setTriggerCount(vk.getTriggerCount() + 1);
                        keywordRepo.save(vk);
                        log.warn("[VOICE] Keyword triggered for user {} — auto-emergency", user.getId());
                    }
                    return match;
                })
                .orElse(false);
    }

    /** Disable keyword without deleting it */
    public void disableKeyword(User user) {
        keywordRepo.findByUser(user).ifPresent(vk -> {
            vk.setActive(false);
            keywordRepo.save(vk);
        });
    }

    /** Get keyword status (not the hash) */
    public Map<String, Object> getStatus(User user) {
        return keywordRepo.findByUser(user)
                // FIX APPLIED HERE: Added <String, Object>
                .map(vk -> Map.<String, Object>of(
                        "active", vk.isActive(),
                        "hint", vk.getHint() != null ? vk.getHint() : "",
                        "triggerCount", vk.getTriggerCount(),
                        "lastTriggeredAt", vk.getLastTriggeredAt() != null
                                ? vk.getLastTriggeredAt().toString() : "never"
                ))
                // FIX APPLIED HERE: Added <String, Object>
                .orElse(Map.<String, Object>of(
                        "active", false,
                        "message", "No keyword set"
                ));
    }
}