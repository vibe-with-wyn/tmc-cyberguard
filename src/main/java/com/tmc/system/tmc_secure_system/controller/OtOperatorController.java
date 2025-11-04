package com.tmc.system.tmc_secure_system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tmc.system.tmc_secure_system.util.excel.ExcelValidator;

@Controller
public class OtOperatorController {

    @PreAuthorize("hasRole('OT_OPERATOR')")
    @GetMapping("/api/ot/home")
    public String home() {
        return "dashboard/ot-operator";
    }

    @PreAuthorize("hasRole('OT_OPERATOR')")
    @PostMapping("/api/ot/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "Please select an Excel file (.xlsx).");
            return "dashboard/ot-operator";
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            model.addAttribute("error", "Only .xlsx files are supported.");
            return "dashboard/ot-operator";
        }
        try {
            var result = ExcelValidator.validate(file.getInputStream());
            if (result.valid()) {
                model.addAttribute("success", result.message());
            } else {
                model.addAttribute("error", result.message());
            }
        } catch (Exception ex) {
            model.addAttribute("error", "Upload failed: " + ex.getMessage());
        }
        return "dashboard/ot-operator";
    }
}