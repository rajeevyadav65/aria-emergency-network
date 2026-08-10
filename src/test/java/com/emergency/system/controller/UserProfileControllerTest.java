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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserProfileControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("PATCH /api/users/me/profile — updates name")
    void updateProfile_success() throws Exception {
        String token = getAliceToken();

        mvc.perform(patch("/api/users/me/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "Alice Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @DisplayName("PATCH /api/users/me/profile — unauthenticated returns 401")
    void updateProfile_noAuth() throws Exception {
        mvc.perform(patch("/api/users/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "Hacker"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/users/me/password — correct current pw succeeds")
    void changePassword_success() throws Exception {
        String token = getBobToken();

        mvc.perform(patch("/api/users/me/password")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "currentPassword", "demo123",
                        "newPassword", "newSecurePass99"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @DisplayName("PATCH /api/users/me/password — wrong current password returns 400")
    void changePassword_wrongCurrent() throws Exception {
        String token = getAliceToken();

        mvc.perform(patch("/api/users/me/password")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "currentPassword", "wrongpassword",
                        "newPassword", "newSecurePass99"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/users/me/history — returns location history array")
    void getLocationHistory() throws Exception {
        String token = getAliceToken();

        mvc.perform(get("/api/users/me/history")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getAliceToken() throws Exception {
        String body = "{\"email\":\"alice@demo.com\",\"password\":\"demo123\"}";
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    private String getBobToken() throws Exception {
        String body = "{\"email\":\"bob@demo.com\",\"password\":\"demo123\"}";
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }
}
