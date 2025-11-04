package com.tmc.system.tmc_secure_system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CisoController {

    @PreAuthorize("hasRole('CISO')")
    @GetMapping("/api/ciso/home")
    public String home() {
        return "dashboard/ciso";
    }
}
