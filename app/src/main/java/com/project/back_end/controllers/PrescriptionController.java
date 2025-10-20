package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {
    
    // 2. Autowire Dependencies
    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, 
                                Service service,
                                AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 3. Define the savePrescription Method
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate prescription data
            if (prescription.getAppointmentId() == null || 
                prescription.getPatientId() == null || 
                prescription.getDoctorId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required prescription data"));
            }

            if (prescription.getMedications() == null || prescription.getMedications().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "At least one medication is required"));
            }

            // Save the prescription
            ResponseEntity<Map<String, String>> saveResult = prescriptionService.savePrescription(prescription);
            
            // If prescription was saved successfully, update appointment status to completed
            if (saveResult.getStatusCode().is2xxSuccessful()) {
                try {
                    // Update appointment status to completed (status 2)
                    appointmentService.changeStatus(prescription.getAppointmentId(), 2, token);
                } catch (Exception e) {
                    // Log the error but don't fail the prescription save
                    System.err.println("Warning: Failed to update appointment status after prescription save: " + e.getMessage());
                }
            }
            
            return saveResult;
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while saving prescription"));
        }
    }

    // 4. Define the getPrescription Method
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate appointment ID
            if (appointmentId == null || appointmentId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid appointment ID"));
            }

            return prescriptionService.getPrescription(appointmentId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving prescription"));
        }
    }

    // Additional endpoint: Get prescription by prescription ID
    @GetMapping("/id/{prescriptionId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescriptionById(
            @PathVariable String prescriptionId,
            @PathVariable String token) {
        
        // Validate token (doctor or patient can access)
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            // Try patient token if doctor token fails
            validationResponse = service.validateToken(token, "patient");
            if (validationResponse.getStatusCode().isError()) {
                return ResponseEntity.status(validationResponse.getStatusCode())
                        .body(Map.of("error", "Invalid or expired token"));
            }
        }

        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid prescription ID"));
            }

            return prescriptionService.getPrescriptionById(prescriptionId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving prescription"));
        }
    }

    // Additional endpoint: Get prescriptions by patient ID (for doctors)
    @GetMapping("/patient/{patientId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescriptionsByPatient(
            @PathVariable Long patientId,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            if (patientId == null || patientId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid patient ID"));
            }

            return prescriptionService.getPrescriptionsByPatient(patientId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving patient prescriptions"));
        }
    }

    // Additional endpoint: Get patient's own prescriptions
    @GetMapping("/my-prescriptions/{token}")
    public ResponseEntity<Map<String, Object>> getMyPrescriptions(@PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Extract patient ID from token
            // This would require additional logic in TokenService to extract patient ID
            // For now, using a placeholder - in real implementation, you'd extract patient ID from token
            Long patientId = extractPatientIdFromToken(token);
            
            if (patientId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unable to extract patient information from token"));
            }

            return prescriptionService.getPrescriptionsByPatient(patientId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving prescriptions"));
        }
    }

    // Additional endpoint: Update prescription status
    @PatchMapping("/{prescriptionId}/status/{status}/{token}")
    public ResponseEntity<Map<String, String>> updatePrescriptionStatus(
            @PathVariable String prescriptionId,
            @PathVariable String status,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid prescription ID"));
            }

            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }

            return prescriptionService.updatePrescriptionStatus(prescriptionId, status);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while updating prescription status"));
        }
    }

    // Additional endpoint: Delete prescription
    @DeleteMapping("/{prescriptionId}/{token}")
    public ResponseEntity<Map<String, String>> deletePrescription(
            @PathVariable String prescriptionId,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid prescription ID"));
            }

            return prescriptionService.deletePrescription(prescriptionId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while deleting prescription"));
        }
    }

    // Helper method to extract patient ID from token (placeholder implementation)
    private Long extractPatientIdFromToken(String token) {
        // In a real implementation, this would use TokenService to extract patient ID
        // For now, return null - this should be implemented based on your TokenService
        return null;
    }

    // Additional endpoint: Get prescriptions by doctor ID
    @GetMapping("/doctor/{doctorId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescriptionsByDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            if (doctorId == null || doctorId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid doctor ID"));
            }

            return prescriptionService.getPrescriptionsByDoctor(doctorId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving doctor prescriptions"));
        }
    }
}