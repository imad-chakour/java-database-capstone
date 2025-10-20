package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    // 2. Constructor Injection for Dependencies
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final MainService validationService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                            PatientRepository patientRepository,
                            DoctorRepository doctorRepository,
                            TokenService tokenService,
                            MainService validationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.validationService = validationService;
    }

    // 4. Book Appointment Method
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            // Validate required fields
            if (appointment.getDoctor() == null || appointment.getPatient() == null || 
                appointment.getAppointmentTime() == null) {
                return 0;
            }
            
            // Check if doctor exists
            Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId());
            if (doctor.isEmpty()) {
                return 0;
            }
            
            // Check if patient exists
            Optional<Patient> patient = patientRepository.findById(appointment.getPatient().getId());
            if (patient.isEmpty()) {
                return 0;
            }
            
            // Check for conflicting appointments
            LocalDateTime appointmentTime = appointment.getAppointmentTime();
            LocalDateTime startTime = appointmentTime.minusMinutes(30);
            LocalDateTime endTime = appointmentTime.plusMinutes(30);
            
            List<Appointment> conflictingAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                    appointment.getDoctor().getId(), startTime, endTime);
            
            if (!conflictingAppointments.isEmpty()) {
                return 0; // Conflict found
            }
            
            // Set default status if not provided
            if (appointment.getStatus() == 0) {
                appointment.setStatus(1); // Default to scheduled status
            }
            
            appointmentRepository.save(appointment);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Failure
        }
    }

    // 5. Update Appointment Method
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Check if appointment exists
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());
            if (existingAppointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Appointment existing = existingAppointment.get();
            
            // Validate patient ID matches
            if (!existing.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("message", "Patient ID mismatch");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if appointment can be updated (not completed or cancelled)
            if (existing.getStatus() == 2 || existing.getStatus() == 3) { // Completed or cancelled
                response.put("message", "Cannot update completed or cancelled appointment");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate doctor availability if time changed
            if (!existing.getAppointmentTime().equals(appointment.getAppointmentTime()) ||
                !existing.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                
                LocalDateTime appointmentTime = appointment.getAppointmentTime();
                LocalDateTime startTime = appointmentTime.minusMinutes(30);
                LocalDateTime endTime = appointmentTime.plusMinutes(30);
                
                List<Appointment> conflictingAppointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                        appointment.getDoctor().getId(), startTime, endTime);
                
                // Remove the current appointment from conflicts
                conflictingAppointments.removeIf(conflict -> conflict.getId().equals(appointment.getId()));
                
                if (!conflictingAppointments.isEmpty()) {
                    response.put("message", "Doctor not available at the specified time");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            // Update appointment fields
            existing.setAppointmentTime(appointment.getAppointmentTime());
            existing.setDoctor(appointment.getDoctor());
            existing.setStatus(appointment.getStatus());
            
            appointmentRepository.save(existing);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Error updating appointment");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 6. Cancel Appointment Method
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate token and extract patient ID
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null) {
                response.put("message", "Invalid token");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Find appointment
            Optional<Appointment> appointment = appointmentRepository.findById(id);
            if (appointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Appointment existing = appointment.get();
            
            // Verify patient owns the appointment
            if (!existing.getPatient().getId().equals(patientId)) {
                response.put("message", "You can only cancel your own appointments");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if appointment can be cancelled
            if (existing.getStatus() == 2) { // Completed
                response.put("message", "Cannot cancel completed appointment");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update status to cancelled instead of deleting
            existing.setStatus(3); // 3 = cancelled status
            appointmentRepository.save(existing);
            
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 7. Get Appointments Method
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract doctor ID from token
            Long doctorId = tokenService.extractDoctorId(token);
            if (doctorId == null) {
                response.put("error", "Invalid token");
                return response;
            }
            
            // Calculate date range for the entire day
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.atTime(23, 59, 59);
            
            List<Appointment> appointments;
            
            if (pname != null && !pname.trim().isEmpty()) {
                // Filter by patient name
                appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctorId, pname.trim(), startDateTime, endDateTime);
            } else {
                // Get all appointments for the day
                appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startDateTime, endDateTime);
            }
            
            response.put("appointments", appointments);
            response.put("count", appointments.size());
            
        } catch (Exception e) {
            response.put("error", "Error retrieving appointments");
        }
        
        return response;
    }

    // 8. Change Status Method
    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(long id, int status, String token) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate token and extract doctor ID
            Long doctorId = tokenService.extractDoctorId(token);
            if (doctorId == null) {
                response.put("message", "Invalid token");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Find appointment
            Optional<Appointment> appointment = appointmentRepository.findById(id);
            if (appointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Appointment existing = appointment.get();
            
            // Verify doctor owns the appointment
            if (!existing.getDoctor().getId().equals(doctorId)) {
                response.put("message", "You can only update your own appointments");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update status
            existing.setStatus(status);
            appointmentRepository.save(existing);
            
            response.put("message", "Appointment status updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Error updating appointment status");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}