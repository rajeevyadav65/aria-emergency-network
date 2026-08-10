package com.emergency.system.controller;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private EmergencyRepository repository;

    @GetMapping("/active-emergencies")
    public List<Emergency> getActiveEmergencies() {
        // Sirf PENDING aur ACTIVE status wali emergencies fetch karega
        return repository.findAll().stream()
                .filter(e -> e.getStatus() != Emergency.EmergencyStatus.RESOLVED)
                .toList();
    }
}