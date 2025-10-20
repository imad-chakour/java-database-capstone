package com.project.back_end.mvc;

import com.project.back_end.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    // 2. Autowire the Shared Service:
    @Autowired
    private MainService service;

    // 3. Define the `adminDashboard` Method:
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Validate the token for admin role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        
        // If validation response is successful, token is valid
        if (validationResponse.getStatusCode().is2xxSuccessful()) {
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
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        
        // If validation response is successful, token is valid
        if (validationResponse.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        } else {
            // Redirect to login page if token is invalid
            return "redirect:/";
        }
    }

    // Add patient dashboard endpoint
    @GetMapping("/patientDashboard/{token}")
    public String patientDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        
        if (validationResponse.getStatusCode().is2xxSuccessful()) {
            return "patient/patientDashboard";
        } else {
            return "redirect:/";
        }
    }
    
}