package com.emergency.system.controller;

import com.emergency.system.model.Alert;
import com.emergency.system.model.User;
import com.emergency.system.repository.AlertRepository;
import com.emergency.system.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints for retrieving alerts received by the current user.
 *
 * GET /api/alerts/mine             — all alerts for authenticated user
 * PATCH /api/alerts/{id}/acknowledge — mark an alert as acknowledged
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    /**
     * Returns all alerts that were sent to the currently authenticated user.
     * Requires JWT token.
     */
    @GetMapping("/mine")
    public ResponseEntity<?> getMyAlerts(
            @AuthenticationPrincipal UserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> ResponseEntity.ok(alertRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Acknowledges an alert — marks the user has seen it.
     */
    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<?> acknowledge(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return alertRepository.findById(id)
                .map(alert -> {
                    if (alert.getStatus() == Alert.AlertStatus.SENT) {
                        alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
                        alertRepository.save(alert);
                    }
                    return ResponseEntity.ok(alert);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Count of unacknowledged alerts for the current user.
     * Used by the frontend badge counter.
     */
    @GetMapping("/mine/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @AuthenticationPrincipal UserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> {
                    long count = alertRepository.findByUser(user).stream()
                            .filter(a -> a.getStatus() == Alert.AlertStatus.SENT)
                            .count();
                    return ResponseEntity.ok(Map.of("unreadCount", count));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
