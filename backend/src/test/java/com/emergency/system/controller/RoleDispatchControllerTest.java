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
class RoleDispatchControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("GET /api/dispatch/doctors — returns on-duty doctors array")
    void onDutyDoctors_returnsArray() throws Exception {
        mvc.perform(get("/api/dispatch/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/dispatch/police — returns police array")
    void onDutyPolice_returnsArray() throws Exception {
        mvc.perform(get("/api/dispatch/police"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/dispatch/ambulances — returns ambulance array")
    void availableAmbulances_returnsArray() throws Exception {
        mvc.perform(get("/api/dispatch/ambulances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /api/dispatch/duty — unauthenticated returns 401")
    void toggleDuty_unauthenticated() throws Exception {
        mvc.perform(patch("/api/dispatch/duty")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"onDuty\":true}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/dispatch/duty — doctor can toggle duty status")
    void toggleDuty_doctor() throws Exception {
        String token = loginAs("dr.sharma@aria.com", "doctor123");

        mvc.perform(patch("/api/dispatch/duty")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("onDuty", true, "isAvailable", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onDuty").value(true))
                .andExpect(jsonPath("$.role").value("DOCTOR"));
    }

    @Test
    @DisplayName("POST /api/dispatch/respond — requires auth")
    void respond_requiresAuth() throws Exception {
        mvc.perform(post("/api/dispatch/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"emergencyId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/dispatch/respond — responder acknowledges dispatch")
    void respond_authenticated() throws Exception {
        String token = loginAs("dr.sharma@aria.com", "doctor123");

        mvc.perform(post("/api/dispatch/respond")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "emergencyId", 1,
                        "etaMinutes", "5",
                        "victimDeviceId", "device-alice-002"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responderId").isNumber())
                .andExpect(jsonPath("$.etaMinutes").value("5"));
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
