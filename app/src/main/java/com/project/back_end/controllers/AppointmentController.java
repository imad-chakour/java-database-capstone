package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    // 2. Autowire Dependencies
    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Define the getAppointments Method
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            String decodedPatientName = patientName.equals("null") ? null : patientName;
            
            Map<String, Object> appointments = appointmentService.getAppointment(decodedPatientName, appointmentDate, token);
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format or other error"));
        }
    }

    // 4. Define the bookAppointment Method
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate appointment data
            if (appointment.getDoctor() == null || appointment.getPatient() == null || 
                appointment.getAppointmentTime() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required appointment data"));
            }

            // Check appointment availability
            int validationResult = service.validateAppointment(appointment);
            if (validationResult == -1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Doctor not found"));
            } else if (validationResult == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Selected time slot is not available"));
            }

            // Book the appointment
            int bookingResult = appointmentService.bookAppointment(appointment);
            if (bookingResult == 1) {
                return ResponseEntity.status(201)
                        .body(Map.of("message", "Appointment booked successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Failed to book appointment. Please try again."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while booking appointment"));
        }
    }

    // 5. Define the updateAppointment Method
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate appointment ID
            if (appointment.getId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Appointment ID is required for update"));
            }

            // Update the appointment
            ResponseEntity<Map<String, String>> updateResult = appointmentService.updateAppointment(appointment);
            return updateResult;

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while updating appointment"));
        }
    }

    // 6. Define the cancelAppointment Method
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate patient token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate appointment ID
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid appointment ID"));
            }

            // Cancel the appointment
            ResponseEntity<Map<String, String>> cancelResult = appointmentService.cancelAppointment(id, token);
            return cancelResult;

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while canceling appointment"));
        }
    }

    // Additional endpoint: Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAppointmentById(@PathVariable Long id) {
        try {
            // This would typically require a new method in AppointmentService
            // For now, returning a placeholder response
            return ResponseEntity.ok(Map.of("message", "Appointment details endpoint", "id", id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving appointment details"));
        }
    }

    // Additional endpoint: Change appointment status (for doctors)
    @PatchMapping("/{id}/status/{status}/{token}")
    public ResponseEntity<Map<String, String>> changeAppointmentStatus(
            @PathVariable Long id,
            @PathVariable int status,
            @PathVariable String token) {
        
        // Validate doctor token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            ResponseEntity<Map<String, String>> statusResult = appointmentService.changeStatus(id, status, token);
            return statusResult;

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while updating appointment status"));
        }
    }

    // Additional endpoint: Get appointments for patient (alternative endpoint)
    @GetMapping("/patient/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable String token,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String doctorName) {
        
        try {
            // Use the service filter method
            ResponseEntity<Map<String, Object>> filterResult = service.filterPatient(condition, doctorName, token);
            return filterResult;

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while retrieving patient appointments"));
        }
    }
}