package com.emergency.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GlobalExceptionHandler.
 * Verifies validation error responses and error format.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("Register with blank email → 400 with field errors")
    void register_blankEmail_returns400WithFieldErrors() throws Exception {
        String body = """
                {"name":"Test","email":"","password":"validpass123","deviceId":"dev-001"}
                """;

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields").exists())
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    @DisplayName("Register with invalid email format → 400 with email error")
    void register_invalidEmail_returns400() throws Exception {
        String body = """
                {"name":"Test","email":"notanemail","password":"validpass123","deviceId":"dev-001"}
                """;

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    @DisplayName("Register with short password → 400 with password error")
    void register_shortPassword_returns400() throws Exception {
        String body = """
                {"name":"Test","email":"valid@test.com","password":"short","deviceId":"dev-001"}
                """;

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    @DisplayName("Login with blank email → 400")
    void login_blankEmail_returns400() throws Exception {
        String body = """
                {"email":"","password":"somepassword"}
                """;

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    @DisplayName("Login with wrong password → 401 with error message")
    void login_wrongPassword_returns401() throws Exception {
        String body = """
                {"email":"alice@demo.com","password":"wrongpassword"}
                """;

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Duplicate email registration → 400 with error message")
    void register_duplicateEmail_returns400() throws Exception {
        String body = """
                {"name":"Dup","email":"alice@demo.com","password":"validpass123","deviceId":"dup-dev"}
                """;

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Emergency with null lat/lon → 400 with field errors")
    void reportEmergency_nullLatLon_returns400() throws Exception {
        String body = """
                {"message":"test","deviceId":"dev-001"}
                """;

        mvc.perform(post("/api/emergency/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.latitude").exists())
                .andExpect(jsonPath("$.fields.longitude").exists());
    }
}
