package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    // 2. Autowire Dependencies
    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Define the getDoctorAvailability Method
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {
        
        // Validate token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, user);
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            LocalDate availabilityDate = LocalDate.parse(date);
            List<String> availability = doctorService.getDoctorAvailability(doctorId, availabilityDate);
            
            return ResponseEntity.ok(Map.of(
                "doctorId", doctorId,
                "date", date,
                "availableSlots", availability,
                "count", availability.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format or other error"));
        }
    }

    // 4. Define the getDoctor Method
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        try {
            List<Doctor> doctors = doctorService.getDoctors();
            return ResponseEntity.ok(Map.of(
                "doctors", doctors,
                "count", doctors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving doctors list"));
        }
    }

    // 5. Define the saveDoctor Method
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        
        // Validate admin token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate doctor data
            if (doctor.getName() == null || doctor.getEmail() == null || doctor.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required doctor data"));
            }

            int saveResult = doctorService.saveDoctor(doctor);
            
            switch (saveResult) {
                case 1:
                    return ResponseEntity.ok(Map.of("message", "Doctor added to db"));
                case -1:
                    return ResponseEntity.status(409)
                            .body(Map.of("error", "Doctor already exists"));
                case 0:
                default:
                    return ResponseEntity.internalServerError()
                            .body(Map.of("error", "Some internal error occurred"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while saving doctor"));
        }
    }

    // 6. Define the doctorLogin Method
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        try {
            // Validate login data
            if (login.getIdentifier() == null || login.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email and password are required"));
            }

            return doctorService.validateDoctor(login);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error during login"));
        }
    }

    // 7. Define the updateDoctor Method
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        
        // Validate admin token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate doctor data
            if (doctor.getId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Doctor ID is required for update"));
            }

            int updateResult = doctorService.updateDoctor(doctor);
            
            switch (updateResult) {
                case 1:
                    return ResponseEntity.ok(Map.of("message", "Doctor updated"));
                case -1:
                    return ResponseEntity.status(404)
                            .body(Map.of("error", "Doctor not found"));
                case 0:
                default:
                    return ResponseEntity.internalServerError()
                            .body(Map.of("error", "Some internal error occurred"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while updating doctor"));
        }
    }

    // 8. Define the deleteDoctor Method
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate admin token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            // Validate ID
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid doctor ID"));
            }

            int deleteResult = doctorService.deleteDoctor(id);
            
            switch (deleteResult) {
                case 1:
                    return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
                case -1:
                    return ResponseEntity.status(404)
                            .body(Map.of("error", "Doctor not found with id"));
                case 0:
                default:
                    return ResponseEntity.internalServerError()
                            .body(Map.of("error", "Some internal error occurred"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error while deleting doctor"));
        }
    }

    // 9. Define the filter Method
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {
        
        try {
            // Convert "null" strings to actual null values
            String doctorName = "null".equals(name) ? null : name;
            String availableTime = "null".equals(time) ? null : time;
            String specialty = "null".equals(speciality) ? null : speciality;

            Map<String, Object> filteredDoctors = service.filterDoctor(doctorName, specialty, availableTime);
            return ResponseEntity.ok(filteredDoctors);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error filtering doctors"));
        }
    }

    // Additional endpoint: Get doctor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable Long id) {
        try {
            // This would typically require a new method in DoctorService
            // For now, using the existing service to get all and filter
            List<Doctor> allDoctors = doctorService.getDoctors();
            Doctor foundDoctor = allDoctors.stream()
                    .filter(doctor -> doctor.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            
            if (foundDoctor != null) {
                return ResponseEntity.ok(Map.of("doctor", foundDoctor));
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Doctor not found"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving doctor details"));
        }
    }

    // Additional endpoint: Search doctors with query parameters (more flexible)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String time) {
        
        try {
            Map<String, Object> searchResult = service.filterDoctor(name, specialty, time);
            return ResponseEntity.ok(searchResult);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error searching doctors"));
        }
    }

    // Additional endpoint: Update doctor profile (for doctors themselves)
    @PatchMapping("/profile/{token}")
    public ResponseEntity<Map<String, String>> updateDoctorProfile(
            @RequestBody Doctor doctorUpdates,
            @PathVariable String token) {
        
        try {
            // This would require additional logic to validate that the doctor
            // is updating their own profile
            // For now, returning a placeholder response
            return ResponseEntity.ok(Map.of("message", "Profile update endpoint - implementation pending"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error updating doctor profile"));
        }
    }
}