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
class VoiceKeywordControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("POST /api/voice/keyword — requires auth")
    void setKeyword_requiresAuth() throws Exception {
        mvc.perform(post("/api/voice/keyword")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"keyword\":\"HELP123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/voice/keyword — sets keyword successfully")
    void setKeyword_authenticated() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(post("/api/voice/keyword")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("keyword", "HELP123", "hint", "starts with H"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.hint").value("starts with H"));
    }

    @Test
    @DisplayName("GET /api/voice/keyword/status — returns status after keyword set")
    void getStatus_afterSet() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(post("/api/voice/keyword")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("keyword", "SECRET99"))))
                .andExpect(status().isOk());

        mvc.perform(get("/api/voice/keyword/status")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.triggerCount").isNumber());
    }

    @Test
    @DisplayName("POST /api/voice/keyword/disable — disables keyword")
    void disableKeyword() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(post("/api/voice/keyword/disable")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/voice/trigger — triggers silent emergency")
    void voiceTrigger_authenticated() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(post("/api/voice/trigger")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "latitude", 27.1767,
                        "longitude", 78.0081,
                        "deviceId", "device-alice-002"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triggered").value(true))
                .andExpect(jsonPath("$.emergencyId").isNumber())
                .andExpect(jsonPath("$.riskLevel").value("HIGH"));
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
