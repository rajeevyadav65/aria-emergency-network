package com.emergency.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class MedicalControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/medical/doctors/nearby — public, returns array")
    void nearbyDoctors_isPublicAndReturnsArray() throws Exception {
        mvc.perform(get("/api/medical/doctors/nearby")
                .param("lat", "27.1767")
                .param("lon", "78.0081"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/medical/ambulances/nearby — returns available units")
    void nearbyAmbulances_returnsArray() throws Exception {
        mvc.perform(get("/api/medical/ambulances/nearby")
                .param("lat", "27.1767")
                .param("lon", "78.0081"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/medical/police/nearby — returns police units")
    void nearbyPolice_returnsArray() throws Exception {
        mvc.perform(get("/api/medical/police/nearby")
                .param("lat", "27.1767")
                .param("lon", "78.0081"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/dispatch/doctors — returns on-duty doctors")
    void onDutyDoctors_returnsArray() throws Exception {
        mvc.perform(get("/api/dispatch/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /api/dispatch/duty — requires auth")
    void toggleDuty_requiresAuth() throws Exception {
        mvc.perform(patch("/api/dispatch/duty")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"onDuty\":true}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/dispatch/duty — authenticated doctor can toggle duty")
    void toggleDuty_authenticated() throws Exception {
        String token = loginAs("dr.sharma@aria.com", "doctor123");
        mvc.perform(patch("/api/dispatch/duty")
                .header("Authorization", "Bearer " + token)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"onDuty\":false,\"isAvailable\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onDuty").value(false));
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
