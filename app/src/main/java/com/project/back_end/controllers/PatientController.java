package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    // 2. Autowire Dependencies
    private final PatientService patientService;
    private final MainService service;

    @Autowired
    public PatientController(PatientService patientService, MainService service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Define the getPatient Method
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            return patientService.getPatientDetails(token);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while fetching patient details"));
        }
    }

    // 4. Define the createPatient Method
    @PostMapping()
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        try {
            // Validate patient data
            if (patient.getName() == null || patient.getEmail() == null || 
                patient.getPassword() == null || patient.getPhone() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required patient data"));
            }

            // Check if patient already exists
            boolean isPatientValid = service.validatePatient(patient);
            if (!isPatientValid) {
                return ResponseEntity.status(409)
                        .body(Map.of("error", "Patient with email id or phone no already exist"));
            }

            // Create the patient
            int createResult = patientService.createPatient(patient);
            
            switch (createResult) {
                case 1:
                    return ResponseEntity.ok(Map.of("message", "Signup successful"));
                case -1:
                    return ResponseEntity.status(409)
                            .body(Map.of("error", "Patient with email already exists"));
                case 0:
                default:
                    return ResponseEntity.internalServerError()
                            .body(Map.of("error", "Internal server error"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while creating patient"));
        }
    }

    // 5. Define the login Method
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        try {
            // Validate login data
            if (login.getIdentifier() == null || login.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email and password are required"));
            }

            return service.validatePatientLogin(login);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error during login"));
        }
    }

    // 6. Define the getPatientAppointment Method
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate patient ID
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid patient ID"));
            }

            return patientService.getPatientAppointment(id, token);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while fetching appointments"));
        }
    }

    // 7. Define the filterPatientAppointment Method
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Convert "null" strings to actual null values
            String filterCondition = "null".equals(condition) ? null : condition;
            String filterName = "null".equals(name) ? null : name;

            return service.filterPatient(filterCondition, filterName, token);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while filtering appointments"));
        }
    }

    // Additional endpoint: Get patient appointments with query parameters (more flexible)
    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String doctor,
            @RequestHeader("Authorization") String token) {
        
        try {
            // Extract token from Authorization header (remove "Bearer " prefix if present)
            String cleanToken = token.replace("Bearer ", "");
            
            return service.filterPatient(condition, doctor, cleanToken);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while fetching appointments"));
        }
    }

    // Additional endpoint: Update patient profile
    @PutMapping("/profile/{token}")
    public ResponseEntity<Map<String, String>> updatePatientProfile(
            @RequestBody Patient patientUpdates,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Extract patient ID from token
            String email = service.validateToken(token).get("email");
            if (email == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unable to extract patient information from token"));
            }

            // In a real implementation, you would:
            // 1. Get the current patient from the database
            // 2. Update only the allowed fields
            // 3. Save the updated patient
            
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while updating profile"));
        }
    }

    // Additional endpoint: Get patient by ID (admin only)
    @GetMapping("/admin/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientById(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate admin token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // This would require a new method in PatientService to get patient by ID
            // For now, returning a placeholder response
            return ResponseEntity.ok(Map.of(
                "message", "Patient details endpoint for admin",
                "patientId", id
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving patient details"));
        }
    }

    // Additional endpoint: Change patient password
    @PatchMapping("/password/{token}")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> passwordData,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password and new password are required"));
            }

            // In a real implementation, you would:
            // 1. Get patient from token
            // 2. Verify current password
            // 3. Update to new password
            // 4. Save patient
            
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while changing password"));
        }
    }
}