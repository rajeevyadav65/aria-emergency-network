package com.emergency.system.controller;

import com.emergency.system.model.LocationHistory;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User profile management endpoints.
 * All require authentication.
 *
 * PATCH /api/users/me/profile   — update name
 * PATCH /api/users/me/password  — change password
 * GET   /api/users/me/history   — location trail (last 24h)
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;

    /** Update display name */
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {

        if (principal == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> {
                    if (body.containsKey("name") && !body.get("name").isBlank())
                        user.setName(body.get("name"));
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Change password — requires current password for verification */
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {

        if (principal == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        String currentPw = body.get("currentPassword");
        String newPw     = body.get("newPassword");

        if (currentPw == null || newPw == null || newPw.length() < 8)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "newPassword must be at least 8 characters"));

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> {
                    if (!passwordEncoder.matches(currentPw, user.getPassword()))
                        return ResponseEntity.status(400)
                                .body(Map.of("error", "Current password is incorrect"));
                    user.setPassword(passwordEncoder.encode(newPw));
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Location movement trail (last 24h) */
    @GetMapping("/history")
    public ResponseEntity<?> getLocationHistory(
            @AuthenticationPrincipal UserDetails principal) {

        if (principal == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> {
                    List<LocationHistory> history =
                            locationService.getLocationHistory(user.getDeviceId());
                    return ResponseEntity.ok(history);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
