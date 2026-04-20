package com.emergency.system.service;

import com.emergency.system.config.JwtUtil;
import com.emergency.system.dto.EmergencyDTOs.*;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy; // Naya import
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles user registration, login, and guest sessions.
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // FIX: Constructor mein @Lazy add kiya gaya hai dependencies ke sath
    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        String deviceId = (request.getDeviceId() != null && !request.getDeviceId().isBlank())
                ? request.getDeviceId()
                : UUID.randomUUID().toString();

        User.UserRole role = User.UserRole.USER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = User.UserRole.valueOf(request.getRole().toUpperCase());
                if (role == User.UserRole.ADMIN || role == User.UserRole.GUEST) {
                    role = User.UserRole.USER;
                }
            } catch (IllegalArgumentException ignored) {}
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .deviceId(deviceId)
                .role(role)
                .licenseNumber(request.getLicenseNumber())
                .specialization(request.getSpecialization())
                .vehicleId(request.getVehicleId())
                .isOnDuty(false)
                .isAvailable(role == User.UserRole.AMBULANCE)
                .build();

        userRepository.save(user);
        log.info("Registered user: {}", user.getEmail());

        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .deviceId(deviceId)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .deviceId(user.getDeviceId())
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse guestSession(String deviceId) {
        String id = (deviceId != null && !deviceId.isBlank()) ? deviceId : UUID.randomUUID().toString();

        if (!userRepository.existsByDeviceId(id)) {
            userRepository.save(User.builder()
                    .deviceId(id)
                    .role(User.UserRole.GUEST)
                    .build());
            log.info("Created guest session: {}", id);
        }

        return AuthResponse.builder()
                .token(jwtUtil.generateToken("guest:" + id))
                .deviceId(id)
                .role("GUEST")
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getEmail())
                        .password(u.getPassword() != null ? u.getPassword() : "")
                        .roles(u.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}