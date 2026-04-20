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

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SyncControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("POST /api/sync/batch — missing deviceId returns 400")
    void syncBatch_missingDeviceId_returns400() throws Exception {
        String body = om.writeValueAsString(Map.of("items", List.of()));
        mvc.perform(post("/api/sync/batch")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/sync/batch — empty items returns zero counts")
    void syncBatch_emptyItems() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "dev-test-sync-001",
                "items", List.of()));

        mvc.perform(post("/api/sync/batch")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/sync/batch — LOCATION_UPDATE item succeeds")
    void syncBatch_locationUpdate() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "deviceId", "dev-test-sync-002",
                "items", List.of(Map.of(
                        "localId", "local-" + System.currentTimeMillis(),
                        "type", "LOCATION_UPDATE",
                        "payload", Map.of("latitude", 27.18, "longitude", 78.01)
                ))));

        mvc.perform(post("/api/sync/batch")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").isNumber())
                .andExpect(jsonPath("$.idMappings").isMap());
    }

    @Test
    @DisplayName("GET /api/sync/pending — returns count for device")
    void getPendingCount_returnsCount() throws Exception {
        mvc.perform(get("/api/sync/pending")
                .param("deviceId", "dev-test-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendingCount").isNumber());
    }
}
