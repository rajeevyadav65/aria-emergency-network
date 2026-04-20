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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/admin/stats — non-admin returns 403")
    void stats_withoutAdminRole_returns403() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/stats — admin returns full stats")
    void stats_asAdmin_returnsStats() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");

        mvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmergencies").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber())
                .andExpect(jsonPath("$.resolutionRatePercent").isNumber())
                .andExpect(jsonPath("$.falseAlarmRatePercent").isNumber());
    }

    @Test
    @DisplayName("GET /api/admin/users — admin returns user list")
    void users_asAdmin_returnsList() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");

        mvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("GET /api/admin/users — without token returns 401")
    void users_withoutToken_returns401() throws Exception {
        mvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/admin/users/{id}/role — admin can update role")
    void updateRole_asAdmin() throws Exception {
        String adminToken = loginAs("admin@aria.com", "admin123");

        // Get Alice's ID first
        String usersResp = mvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andReturn().getResponse().getContentAsString();
        var users = om.readTree(usersResp);
        Long aliceId = null;
        for (var u : users) {
            if ("alice@demo.com".equals(u.get("email").asText())) {
                aliceId = u.get("id").asLong();
                break;
            }
        }

        if (aliceId != null) {
            mvc.perform(patch("/api/admin/users/" + aliceId + "/role?role=USER")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("USER"));
        }
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
