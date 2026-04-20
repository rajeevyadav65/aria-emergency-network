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
class EmergencyExportControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/export/emergencies.csv — non-admin returns 403")
    void export_withoutAdmin_returns403() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(get("/api/export/emergencies.csv")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/export/emergencies.csv — admin gets CSV with headers")
    void export_asAdmin_returnsCsv() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");

        mvc.perform(get("/api/export/emergencies.csv")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id,message,riskLevel")));
    }

    @Test
    @DisplayName("GET /api/export/emergencies.csv — returns attachment filename")
    void export_hasDownloadFilename() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");

        mvc.perform(get("/api/export/emergencies.csv")
                .header("Authorization", "Bearer " + token))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString(".csv")));
    }

    @Test
    @DisplayName("GET /api/export/emergencies.csv?riskLevel=HIGH — filtered export")
    void export_filteredByRisk() throws Exception {
        String token = loginAs("admin@aria.com", "admin123");

        mvc.perform(get("/api/export/emergencies.csv")
                .param("riskLevel", "HIGH")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Record-Count"));
    }

    @Test
    @DisplayName("GET /api/export/emergencies.csv — unauthenticated returns 401")
    void export_noAuth_returns401() throws Exception {
        mvc.perform(get("/api/export/emergencies.csv"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
