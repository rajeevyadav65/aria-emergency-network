package com.emergency.system.config;

import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check karega ki DB khali hai ya nahi
            if (userRepository.count() == 0) {

                // 1. Create Demo Normal User
                userRepository.save(User.builder()
                        .name("Alice (Demo User)")
                        .email("alice@demo.com")
                        .password(passwordEncoder.encode("demo123"))
                        .deviceId("demo-device-1")
                        .role(User.UserRole.USER)
                        .preferredLanguage("en")
                        .build());

                // 2. Create Demo Doctor
                userRepository.save(User.builder()
                        .name("Dr. Sharma")
                        .email("dr.sharma@aria.com")
                        .password(passwordEncoder.encode("doctor123"))
                        .deviceId("doc-device-1")
                        .role(User.UserRole.DOCTOR)
                        .consultationFee(500.0) // Doctor ki fees
                        .specialization("Cardiologist")
                        .preferredLanguage("hi")
                        .build());

                // 3. Create System Admin
                userRepository.save(User.builder()
                        .name("System Admin")
                        .email("admin@aria.com")
                        .password(passwordEncoder.encode("admin123"))
                        .deviceId("admin-device-1")
                        .role(User.UserRole.ADMIN)
                        .build());

                System.out.println("✅ All Demo Users Auto-Created Successfully!");
            }
        };
    }
}