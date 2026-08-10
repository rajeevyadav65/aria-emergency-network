package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.AuthResponse;
import com.emergency.system.dto.EmergencyDTOs.LoginRequest;
import com.emergency.system.dto.EmergencyDTOs.RegisterRequest;
import com.emergency.system.service.OtpService;
import com.emergency.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/guest")
    public ResponseEntity<AuthResponse> guestSession(
            @RequestParam(required = false) String deviceId) {
        return ResponseEntity.ok(userService.guestSession(deviceId));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String contact = req.get("contact");
        otpService.generateAndSendOtp(contact);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + contact));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String contact = req.get("contact");
        String otp = req.get("otp");
        String newPassword = req.get("newPassword");

        if (otpService.verifyOtp(contact, otp)) {
            return ResponseEntity.ok(Map.of("message", "Password reset successfully! You can login now."));
        }
        return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP!"));
    }
}
