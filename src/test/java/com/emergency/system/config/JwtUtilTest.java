package com.emergency.system.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtUtil — no Spring context needed.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET =
            "test-jwt-secret-key-minimum-32-characters-long";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86_400_000L); // 24h
    }

    @Test
    @DisplayName("generateToken returns a non-blank JWT string")
    void generateToken_returnsToken() {
        String token = jwtUtil.generateToken("alice@demo.com");
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("extractSubject returns the subject used during generation")
    void extractSubject_roundTrip() {
        String subject = "alice@demo.com";
        String token = jwtUtil.generateToken(subject);
        assertThat(jwtUtil.extractSubject(token)).isEqualTo(subject);
    }

    @Test
    @DisplayName("isTokenValid returns true for a fresh token")
    void isTokenValid_freshToken_true() {
        String token = jwtUtil.generateToken("bob@demo.com");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid returns false for an expired token")
    void isTokenValid_expiredToken_false() {
        // Generate a token that expired 1 second ago
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generateToken("expired@demo.com");
        assertThat(jwtUtil.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for a tampered token")
    void isTokenValid_tamperedToken_false() {
        String token = jwtUtil.generateToken("alice@demo.com");
        // Corrupt the signature
        String tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertThat(jwtUtil.isTokenValid(tampered)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for garbage input")
    void isTokenValid_garbage_false() {
        assertThat(jwtUtil.isTokenValid("not.a.jwt")).isFalse();
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }

    @Test
    @DisplayName("Guest token subject prefixed with guest:")
    void guestTokenSubject() {
        String token = jwtUtil.generateToken("guest:device-abc123");
        String subject = jwtUtil.extractSubject(token);
        assertThat(subject).startsWith("guest:");
        assertThat(subject).contains("device-abc123");
    }

    @Test
    @DisplayName("Different subjects produce different tokens")
    void differentSubjects_differentTokens() {
        String t1 = jwtUtil.generateToken("alice@demo.com");
        String t2 = jwtUtil.generateToken("bob@demo.com");
        assertThat(t1).isNotEqualTo(t2);
    }
}
