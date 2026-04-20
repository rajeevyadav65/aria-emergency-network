package com.emergency.system.controller;

import com.emergency.system.config.JwtUtil;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        try {
            String contact = req.get("contact");
            String email = null;
            String phone = null;

            if (contact != null && contact.contains("@")) {
                email = contact;
            } else {
                phone = contact;
            }

            // 🟢 NAYA: Dynamic Role Selection (Doctor/Police/User)
            String roleStr = req.get("role");
            User.UserRole role = (roleStr != null && !roleStr.isBlank())
                    ? User.UserRole.valueOf(roleStr.toUpperCase())
                    : User.UserRole.USER;

            User user = User.builder()
                    .name(req.get("name"))
                    .email(email)
                    .phone(phone)
                    .password(passwordEncoder.encode(req.get("password")))
                    .deviceId(req.get("deviceId") + "-" + System.currentTimeMillis())
                    .preferredLanguage(req.get("preferredLanguage"))
                    .role(role) // 🟢 Ab Doctor save hoga!
                    .build();

            userRepository.save(user);

            String token = jwtUtil.generateToken(contact);
            return ResponseEntity.ok(Map.of("token", token, "deviceId", user.getDeviceId(), "role", role.name(), "name", user.getName()));
        } catch (Exception e) {
            log.error("Registration failed: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Email or Phone already exists!"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String contact = req.get("contact");
        Optional<User> userOpt = userRepository.findByEmail(contact);

        // Agar aapne findByPhone banaya hai toh yahan use karein
        // if (userOpt.isEmpty()) { userOpt = userRepository.findByPhone(contact); }

        if (userOpt.isPresent() && passwordEncoder.matches(req.get("password"), userOpt.get().getPassword())) {
            String token = jwtUtil.generateToken(contact);
            return ResponseEntity.ok(Map.of("token", token, "name", userOpt.get().getName(), "role", userOpt.get().getRole().name()));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
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