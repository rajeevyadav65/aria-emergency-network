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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiDetectionControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("POST /api/ai/detect/signal — NONE signal returns NONE detectionType")
    void detectSignal_none() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "test-device-001",
                "signal", "NONE",
                "latitude", 27.1767,
                "longitude", 78.0081));

        mvc.perform(post("/api/ai/detect/signal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detectionType").exists())
                .andExpect(jsonPath("$.confidence").isNumber())
                .andExpect(jsonPath("$.triggerEmergency").isBoolean());
    }

    @Test
    @DisplayName("POST /api/ai/detect/signal — FALL signal auto-fires emergency")
    void detectSignal_fall_firesEmergency() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "test-device-fall-001",
                "signal", "FALL",
                "latitude", 27.1767,
                "longitude", 78.0081));

        mvc.perform(post("/api/ai/detect/signal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detectionType").value("FALL"))
                .andExpect(jsonPath("$.triggerEmergency").value(true))
                .andExpect(jsonPath("$.emergencyResponse").exists());
    }

    @Test
    @DisplayName("POST /api/ai/detect/signal — PANIC_FACE triggers emergency")
    void detectSignal_panicFace() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "test-device-panic-001",
                "signal", "PANIC_FACE",
                "latitude", 27.18,
                "longitude", 78.01));

        mvc.perform(post("/api/ai/detect/signal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triggerEmergency").value(true));
    }

    @Test
    @DisplayName("POST /api/ai/detect/frame — missing deviceId returns 400")
    void detectFrame_missingDeviceId() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "imageBase64", "dGVzdA==",
                "latitude", 27.1767, "longitude", 78.0081));

        mvc.perform(post("/api/ai/detect/frame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/ai/detect/signal — is public (no auth required)")
    void detectSignal_isPublic() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "test-dev", "signal", "NONE",
                "latitude", 0.0, "longitude", 0.0));

        mvc.perform(post("/api/ai/detect/signal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk()); // not 401
    }
}
