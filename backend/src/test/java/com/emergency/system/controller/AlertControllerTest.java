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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/alerts/mine — without auth returns 401")
    void myAlerts_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/alerts/mine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/alerts/mine — authenticated user gets array")
    void myAlerts_authenticated_returnsArray() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");
        mvc.perform(get("/api/alerts/mine")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/alerts/mine/unread-count — returns count map")
    void unreadCount_authenticated() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");
        mvc.perform(get("/api/alerts/mine/unread-count")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").isNumber());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
