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
class DisasterAlertControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/disasters/active — public, returns array")
    void getActive_isPublic() throws Exception {
        mvc.perform(get("/api/disasters/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/disasters/nearby — returns array for valid coords")
    void getNearby_validCoords() throws Exception {
        mvc.perform(get("/api/disasters/nearby")
                .param("lat", "27.1767")
                .param("lon", "78.0081"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/disasters — non-admin returns 403")
    void create_withoutAdmin_returns403() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");
        String body = om.writeValueAsString(Map.of(
                "type", "FLOOD", "title", "Test flood",
                "severity", "MEDIUM"));

        mvc.perform(post("/api/disasters")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/disasters — admin creates alert")
    void create_asAdmin_succeeds() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");
        String body = om.writeValueAsString(Map.of(
                "type", "FIRE",
                "title", "Industrial fire near factory",
                "description", "Large fire reported near industrial zone",
                "severity", "HIGH",
                "epicenterLat", 27.18,
                "epicenterLon", 78.01,
                "radiusKm", 5.0));

        mvc.perform(post("/api/disasters")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("FIRE"))
                .andExpect(jsonPath("$.source").value("MANUAL"));
    }

    @Test
    @DisplayName("PATCH /api/disasters/{id}/resolve — admin resolves alert")
    void resolve_asAdmin() throws Exception {
        // First create an alert
        String token = loginAs("admin@aria.com", "admin123");
        String createBody = om.writeValueAsString(Map.of(
                "type", "CYCLONE", "title", "Test cyclone",
                "severity", "LOW"));

        String createResp = mvc.perform(post("/api/disasters")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = om.readTree(createResp).get("id").asLong();

        // Then resolve it
        mvc.perform(patch("/api/disasters/" + id + "/resolve")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertStatus").value("RESOLVED"));
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
