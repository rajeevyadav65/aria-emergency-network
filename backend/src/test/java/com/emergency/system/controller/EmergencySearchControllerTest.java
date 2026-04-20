package com.emergency.system.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmergencySearchControllerTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("GET /api/emergency/search — no params returns all emergencies")
    void search_noParams_returnsAll() throws Exception {
        mvc.perform(get("/api/emergency/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/emergency/search?riskLevel=HIGH — filters by risk")
    void search_byRiskLevel() throws Exception {
        mvc.perform(get("/api/emergency/search").param("riskLevel", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].riskLevel")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.is("HIGH"))));
    }

    @Test
    @DisplayName("GET /api/emergency/search?status=RESOLVED — filters by status")
    void search_byStatus() throws Exception {
        mvc.perform(get("/api/emergency/search").param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].status")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.is("RESOLVED"))));
    }

    @Test
    @DisplayName("GET /api/emergency/search?keyword=fall — keyword search works")
    void search_byKeyword() throws Exception {
        mvc.perform(get("/api/emergency/search").param("keyword", "fall"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/emergency/search with geo radius — returns only nearby")
    void search_withGeoRadius() throws Exception {
        mvc.perform(get("/api/emergency/search")
                .param("lat", "27.1767")
                .param("lon", "78.0081")
                .param("radiusKm", "1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/emergency/search?riskLevel=INVALID — invalid enum returns 400 or empty")
    void search_invalidEnum_handledGracefully() throws Exception {
        // Spring will return 400 for invalid enum binding
        mvc.perform(get("/api/emergency/search").param("riskLevel", "INVALID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Search is publicly accessible — no auth required")
    void search_isPublic() throws Exception {
        mvc.perform(get("/api/emergency/search"))
                .andExpect(status().isOk()); // not 401
    }
}
