package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for EmergencyController.
 * Uses H2 in-memory DB — no external dependencies.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmergencyControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/emergency/report — public, returns EmergencyResponse")
    void reportEmergency_success() throws Exception {
        EmergencyRequest req = EmergencyRequest.builder()
                .message("Test emergency")
                .latitude(27.1767)
                .longitude(78.0081)
                .fallDetected(false)
                .movement("STATIONARY")
                .userResponse("NEED_HELP")
                .deviceId("test-device-001")
                .build();

        mvc.perform(post("/api/emergency/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyId").exists())
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("POST /api/emergency/report — user OK → FALSE_ALARM")
    void reportEmergency_userOk_falseAlarm() throws Exception {
        EmergencyRequest req = EmergencyRequest.builder()
                .message("Accidental trigger")
                .latitude(27.1767).longitude(78.0081)
                .fallDetected(false).movement("WALKING")
                .userResponse("ARE_YOU_OK")
                .deviceId("test-device-002")
                .build();

        mvc.perform(post("/api/emergency/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALSE_ALARM"));
    }

    @Test
    @DisplayName("GET /api/emergency/active — requires auth")
    void getActive_withoutAuth_returns401or403() throws Exception {
        mvc.perform(get("/api/emergency/active"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /api/emergency/{id} — non-existent returns 404")
    void getById_notFound_returns404() throws Exception {
        String token = obtainToken("alice@demo.com", "demo123");
        mvc.perform(get("/api/emergency/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String obtainToken(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String response = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
