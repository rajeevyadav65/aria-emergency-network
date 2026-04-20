package com.emergency.system.service;

import com.emergency.system.config.JwtUtil;
import com.emergency.system.dto.EmergencyDTOs.*;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authManager;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, passwordEncoder, jwtUtil, authManager);
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register() creates user and returns token")
    void register_success() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Alice").email("alice@test.com")
                .password("password123").deviceId("dev-001").build();

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken("alice@test.com")).thenReturn("jwt-token");

        AuthResponse resp = service.register(req);

        assertThat(resp.getToken()).isEqualTo("jwt-token");
        assertThat(resp.getDeviceId()).isEqualTo("dev-001");
        assertThat(resp.getRole()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() auto-generates deviceId when not provided")
    void register_autoGeneratesDeviceId() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Bob").email("bob@test.com")
                .password("password123").build(); // no deviceId

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse resp = service.register(req);
        assertThat(resp.getDeviceId()).isNotBlank();
    }

    @Test
    @DisplayName("register() throws when email already exists")
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        RegisterRequest req = RegisterRequest.builder()
                .email("dup@test.com").password("pass").build();

        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in use");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login() returns token for valid credentials")
    void login_success() {
        User user = User.builder().email("alice@test.com")
                .deviceId("dev-001").role(User.UserRole.USER).build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("alice@test.com")).thenReturn("jwt-token");

        AuthResponse resp = service.login(
                LoginRequest.builder().email("alice@test.com").password("pass").build());

        assertThat(resp.getToken()).isEqualTo("jwt-token");
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login() propagates BadCredentialsException from authManager")
    void login_badCredentials_throws() {
        doThrow(new BadCredentialsException("bad creds"))
                .when(authManager).authenticate(any());

        assertThatThrownBy(() -> service.login(
                LoginRequest.builder().email("alice@test.com").password("wrong").build()))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ── Guest ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guestSession() creates new guest when device not found")
    void guestSession_newDevice_createsUser() {
        when(userRepository.existsByDeviceId("new-dev")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken("guest:new-dev")).thenReturn("guest-token");

        AuthResponse resp = service.guestSession("new-dev");

        assertThat(resp.getRole()).isEqualTo("GUEST");
        assertThat(resp.getToken()).isEqualTo("guest-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("guestSession() reuses existing device without saving")
    void guestSession_existingDevice_noSave() {
        when(userRepository.existsByDeviceId("exist-dev")).thenReturn(true);
        when(jwtUtil.generateToken("guest:exist-dev")).thenReturn("token");

        service.guestSession("exist-dev");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("guestSession() auto-generates deviceId when null")
    void guestSession_nullDeviceId_generates() {
        when(userRepository.existsByDeviceId(any())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse resp = service.guestSession(null);
        assertThat(resp.getDeviceId()).isNotBlank();
    }

    // ── loadUserByUsername ────────────────────────────────────────────────────

    @Test
    @DisplayName("loadUserByUsername() returns UserDetails for known email")
    void loadUserByUsername_found() {
        User user = User.builder().email("alice@test.com")
                .password("hashed").role(User.UserRole.USER).build();
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        var ud = service.loadUserByUsername("alice@test.com");
        assertThat(ud.getUsername()).isEqualTo("alice@test.com");
    }

    @Test
    @DisplayName("loadUserByUsername() throws UsernameNotFoundException for unknown email")
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
