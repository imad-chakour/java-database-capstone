package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final MainService service;

    @Autowired
    public AdminController(MainService service) {
        this.service = service;
    }

    // 3. Define the adminLogin Method
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        try {
            // Validate login data
            if (admin.getUsername() == null || admin.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username and password are required"));
            }

            return service.validateAdmin(admin);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error during admin login"));
        }
    }

    // Additional endpoint: Validate admin token
    @GetMapping("/validate/{token}")
    public ResponseEntity<Map<String, String>> validateAdminToken(@PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
            return validationResponse;
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error validating admin token"));
        }
    }

    // Additional endpoint: Get admin dashboard data
    @GetMapping("/dashboard/{token}")
    public ResponseEntity<Map<String, Object>> getAdminDashboard(@PathVariable String token) {
        try {
            // Validate admin token first
            ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
            if (validationResponse.getStatusCode().isError()) {
                return ResponseEntity.status(validationResponse.getStatusCode())
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Return dashboard data (you can customize this based on your needs)
            return ResponseEntity.ok(Map.of(
                "message", "Admin dashboard data",
                "stats", Map.of(
                    "totalDoctors", 0,  // You would get these from your services
                    "totalPatients", 0,
                    "totalAppointments", 0
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving admin dashboard data"));
        }
    }
}