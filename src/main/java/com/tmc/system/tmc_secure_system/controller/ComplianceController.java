package com.tmc.system.tmc_secure_system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComplianceController {

    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    @GetMapping("/api/compliance/home")
    public String home() {
        return "dashboard/compliance";
    }
}