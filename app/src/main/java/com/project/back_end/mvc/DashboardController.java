package com.project.back_end.mvc;

import com.project.back_end.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    // 2. Autowire the Shared Service:
    @Autowired
    private Service service;

    // 3. Define the `adminDashboard` Method:
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Validate the token for admin role
        Map<String, String> validationResult = service.validateToken(token, "admin");
        
        // If validation result is empty, token is valid
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard";
        } else {
            // Redirect to login page if token is invalid
            return "redirect:/";
        }
    }

    // 4. Define the `doctorDashboard` Method:
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Validate the token for doctor role
        Map<String, String> validationResult = service.validateToken(token, "doctor");
        
        // If validation result is empty, token is valid
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            // Redirect to login page if token is invalid
            return "redirect:/";
        }
    }
}