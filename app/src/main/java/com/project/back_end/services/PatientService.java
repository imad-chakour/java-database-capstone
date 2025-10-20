package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    // 2. Constructor Injection for Dependencies
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. createPatient Method
    @Transactional
    public int createPatient(Patient patient) {
        try {
            // Check if patient already exists by email
            Patient existingPatient = patientRepository.findByEmail(patient.getEmail());
            if (existingPatient != null) {
                return -1; // Patient already exists
            }

            patientRepository.save(patient);
            return 1; // Success
        } catch (Exception e) {
            // Log the error (you can use a logger here)
            System.err.println("Error creating patient: " + e.getMessage());
            return 0; // Failure
        }
    }

    // 4. getPatientAppointment Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract email from token
            String email = tokenService.extractEmail(token);
            if (email == null) {
                response.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(response);
            }

            // Find patient by email from token
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify patient ID matches
            if (!patient.getId().equals(id)) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }

            // Get patient appointments
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            
            // Convert to DTOs
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("count", appointmentDTOs.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 5. filterByCondition Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Appointment> appointments;
            
            if ("past".equalsIgnoreCase(condition)) {
                // Get past appointments (status 1 = completed)
                appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, 1);
            } else if ("future".equalsIgnoreCase(condition)) {
                // Get future appointments (status 0 = scheduled)
                appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, 0);
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.badRequest().body(response);
            }

            // Convert to DTOs
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("count", appointmentDTOs.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 6. filterByDoctor Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            
            // Convert to DTOs
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("count", appointmentDTOs.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 7. filterByDoctorAndCondition Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1; // Completed
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0; // Scheduled
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments = appointmentRepository
                .filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
            
            // Convert to DTOs
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("count", appointmentDTOs.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 8. getPatientDetails Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract email from token
            String email = tokenService.extractEmail(token);
            if (email == null) {
                response.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(response);
            }

            // Find patient by email
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Return patient details (excluding sensitive information like password)
            Map<String, Object> patientDetails = new HashMap<>();
            patientDetails.put("id", patient.getId());
            patientDetails.put("name", patient.getName());
            patientDetails.put("email", patient.getEmail());
            patientDetails.put("phone", patient.getPhone());
            patientDetails.put("address", patient.getAddress());
            patientDetails.put("isActive", patient.isActive());

            response.put("patient", patientDetails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Helper method to convert Appointment to AppointmentDTO
    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        return new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getPatient().getEmail(),
            appointment.getPatient().getPhone(),
            appointment.getPatient().getAddress(),
            appointment.getAppointmentTime(),
            appointment.getStatus()
        );
    }

    // Additional helper method to get patient by ID
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    // Additional method to update patient details
    @Transactional
    public ResponseEntity<Map<String, Object>> updatePatient(Long id, Patient patientDetails, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify token and authorization
            String email = tokenService.extractEmail(token);
            Patient existingPatient = patientRepository.findByEmail(email);
            
            if (existingPatient == null || !existingPatient.getId().equals(id)) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }

            // Update patient details
            existingPatient.setName(patientDetails.getName());
            existingPatient.setPhone(patientDetails.getPhone());
            existingPatient.setAddress(patientDetails.getAddress());
            
            patientRepository.save(existingPatient);
            
            response.put("message", "Patient details updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}