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

/**
 * Integration tests for location update and user profile endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LocationUserControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @Test
    @DisplayName("POST /api/location/update — public, accepts lat/lon/deviceId")
    void updateLocation_isPublic() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "latitude",  27.1767,
                "longitude", 78.0081,
                "deviceId",  "test-loc-device-001"));

        mvc.perform(post("/api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/location/update — creates user if deviceId not known")
    void updateLocation_createsGuestUser() throws Exception {
        String body = om.writeValueAsString(Map.of(
                "latitude",  27.18,
                "longitude", 78.01,
                "deviceId",  "brand-new-device-" + System.currentTimeMillis()));

        mvc.perform(post("/api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(27.18));
    }

    @Test
    @DisplayName("GET /api/users/me — requires authentication")
    void getMe_requiresAuth() throws Exception {
        mvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users/me — returns current user profile")
    void getMe_authenticated() throws Exception {
        String token = loginAs("alice@demo.com", "demo123");

        mvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@demo.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("GET /api/users/me — doctor has doctor role")
    void getMe_doctorRole() throws Exception {
        String token = loginAs("dr.sharma@aria.com", "doctor123");

        mvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DOCTOR"));
    }

    @Test
    @DisplayName("GET /api/location/history — returns location trail for device")
    void locationHistory_returnsTrail() throws Exception {
        mvc.perform(get("/api/location/history")
                .param("deviceId", "device-alice-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private String loginAs(String email, String password) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        String resp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(resp).get("token").asText();
    }
}
