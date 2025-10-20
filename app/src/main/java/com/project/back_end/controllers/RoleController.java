package com.project.back_end.controllers;

import com.project.back_end.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final MainService service;

    @Autowired
    public RoleController(MainService service) {
        this.service = service;
    }

    @GetMapping("/selectRole")
    public ResponseEntity<Map<String, Object>> selectRole(@RequestParam String role, @RequestParam String token) {
        // Validate token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, role);
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }
        // Logic to handle role selection
        return ResponseEntity.ok(Map.of("message", "Role selected successfully"));
    }
}
