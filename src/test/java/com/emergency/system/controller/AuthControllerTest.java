package com.emergency.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register — creates user and returns JWT")
    void register_success() throws Exception {
        String email = "newuser_" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
        String body = String.format(
                "{\"name\":\"Test User\",\"email\":\"%s\",\"password\":\"test1234\",\"deviceId\":\"dev-test-001\"}", email);

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.deviceId").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register — duplicate email returns 400")
    void register_duplicateEmail_returns400() throws Exception {
        String body = "{\"name\":\"Alice\",\"email\":\"alice@demo.com\",\"password\":\"test1234\",\"deviceId\":\"dup-dev\"}";

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login — valid credentials return JWT")
    void login_success() throws Exception {
        String body = "{\"email\":\"alice@demo.com\",\"password\":\"demo123\"}";

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.deviceId").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login — wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        String body = "{\"email\":\"alice@demo.com\",\"password\":\"wrongpass\"}";

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/guest — creates guest session with JWT")
    void guestSession_success() throws Exception {
        mvc.perform(post("/api/auth/guest?deviceId=guest-test-" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("GUEST"));
    }
}
