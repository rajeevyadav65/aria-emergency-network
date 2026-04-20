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
class LocationShareControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("POST /api/location/update — updates device location")
    void updateLocation_success() throws Exception {
        mvc.perform(post("/api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "deviceId", "test-loc-device-001",
                        "latitude", 27.1767,
                        "longitude", 78.0081))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("updated"));
    }

    @Test
    @DisplayName("POST /api/location/update — missing deviceId returns 400")
    void updateLocation_missingDeviceId() throws Exception {
        mvc.perform(post("/api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("latitude", 27.17, "longitude", 78.00))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/location/share — starts sharing session")
    void startSharing_success() throws Exception {
        mvc.perform(post("/api/location/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "deviceId", "test-share-device-001",
                        "latitude", 27.1767,
                        "longitude", 78.0081,
                        "targetRole", "POLICE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.targetRole").value("POLICE"))
                .andExpect(jsonPath("$.message").value("Location sharing started"));
    }

    @Test
    @DisplayName("POST /api/location/share — AMBULANCE target works")
    void startSharing_ambulanceTarget() throws Exception {
        mvc.perform(post("/api/location/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "deviceId", "test-share-device-002",
                        "latitude", 27.18,
                        "longitude", 78.01,
                        "targetRole", "AMBULANCE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.targetRole").value("AMBULANCE"));
    }

    @Test
    @DisplayName("DELETE /api/location/share/{id} — stops session")
    void stopSharing_success() throws Exception {
        // Create session first
        String resp = mvc.perform(post("/api/location/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "deviceId", "test-stop-device",
                        "latitude", 27.17,
                        "longitude", 78.00,
                        "targetRole", "POLICE"))))
                .andReturn().getResponse().getContentAsString();

        String sessionId = om.readTree(resp).get("sessionId").asText();

        // Stop it
        mvc.perform(delete("/api/location/share/" + sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId));
    }

    @Test
    @DisplayName("GET /api/location/history/{deviceId} — returns location data")
    void getHistory_returnsArray() throws Exception {
        // First update location so history exists
        mvc.perform(post("/api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "deviceId", "device-alice-002",
                        "latitude", 27.1767,
                        "longitude", 78.0081))));

        mvc.perform(get("/api/location/history/device-alice-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
