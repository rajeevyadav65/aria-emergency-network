package com.emergency.system.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmergencyAnalyticsControllerTest {

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("GET /api/analytics/timeline — public, returns labels and values")
    void timeline_returnsData() throws Exception {
        mvc.perform(get("/api/analytics/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.values").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.labels.length()").value(24));
    }

    @Test
    @DisplayName("GET /api/analytics/risk — returns risk level breakdown")
    void riskBreakdown_returnsData() throws Exception {
        mvc.perform(get("/api/analytics/risk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.values").isArray());
    }

    @Test
    @DisplayName("GET /api/analytics/trend — returns 7-day trend")
    void trend_returns7Days() throws Exception {
        mvc.perform(get("/api/analytics/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels.length()").value(7))
                .andExpect(jsonPath("$.changePercent").isNumber());
    }

    @Test
    @DisplayName("GET /api/analytics/hotspots — returns hotspot list")
    void hotspots_returnsData() throws Exception {
        mvc.perform(get("/api/analytics/hotspots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotspots").isArray())
                .andExpect(jsonPath("$.totalActive").isNumber());
    }

    @Test
    @DisplayName("GET /api/analytics/roles — returns user role breakdown")
    void roleBreakdown_returnsData() throws Exception {
        mvc.perform(get("/api/analytics/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    @DisplayName("GET /api/analytics/status — returns status breakdown")
    void statusBreakdown_returnsData() throws Exception {
        mvc.perform(get("/api/analytics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.values").isArray());
    }

    @Test
    @DisplayName("All analytics endpoints are public — no auth required")
    void analytics_arePublic() throws Exception {
        // No Authorization header — should still return 200
        mvc.perform(get("/api/analytics/timeline")).andExpect(status().isOk());
        mvc.perform(get("/api/analytics/trend")).andExpect(status().isOk());
        mvc.perform(get("/api/analytics/roles")).andExpect(status().isOk());
    }
}
