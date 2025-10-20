package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Service {

    // 2. Constructor Injection for Dependencies
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                  AdminRepository adminRepository,
                  DoctorRepository doctorRepository,
                  PatientRepository patientRepository,
                  DoctorService doctorService,
                  PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 3. validateToken Method
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        
        try {
            boolean isValid = tokenService.validateToken(token);
            if (!isValid) {
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(401).body(response);
            }
            
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Error validating token");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 4. validateAdmin Method
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find admin by username
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                response.put("error", "Admin not found");
                return ResponseEntity.status(401).body(response);
            }

            // Verify password (in real application, use password encoder)
            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }

            // Generate token
            String token = tokenService.generateToken(admin.getUsername(), "admin");
            response.put("token", token);
            response.put("message", "Admin login successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error during admin validation");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 5. filterDoctor Method
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Apply different filters based on provided parameters
            if (name != null && specialty != null && time != null) {
                // Filter by name, specialty, and time
                return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
            } else if (name != null && specialty != null) {
                // Filter by name and specialty
                return doctorService.filterDoctorByNameAndSpecility(name, specialty);
            } else if (name != null && time != null) {
                // Filter by name and time
                return doctorService.filterDoctorByNameAndTime(name, time);
            } else if (specialty != null && time != null) {
                // Filter by specialty and time
                return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
            } else if (name != null) {
                // Filter by name only
                return doctorService.findDoctorByName(name);
            } else if (specialty != null) {
                // Filter by specialty only
                return doctorService.filterDoctorBySpecility(specialty);
            } else if (time != null) {
                // Filter by time only
                return doctorService.filterDoctorsByTime(time);
            } else {
                // No filters - return all doctors
                List<Doctor> allDoctors = doctorService.getDoctors();
                response.put("doctors", allDoctors);
                response.put("count", allDoctors.size());
                return response;
            }
            
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", List.of());
            response.put("count", 0);
            return response;
        }
    }

    // 6. validateAppointment Method
    public int validateAppointment(Appointment appointment) {
        try {
            // Use getDoctorId() instead of getDoctor().getId()
            Long doctorId = appointment.getDoctorId();
            if (doctorId == null) {
                return -1; // Doctor ID is null
            }
    
            // Check if doctor exists
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return -1; // Doctor doesn't exist
            }
    
            Doctor doctor = doctorOpt.get();
            LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
            
            // Get available time slots for the doctor on the appointment date
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointmentDate);
            
            // Check if appointment time matches any available slot
            LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
            String requestedSlot = appointmentTime.toString();
            
            if (availableSlots.contains(requestedSlot)) {
                return 1; // Valid appointment time
            } else {
                return 0; // Time unavailable
            }
            
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    // 7. validatePatient Method
    public boolean validatePatient(Patient patient) {
        try {
            // Check if patient already exists by email or phone
            Patient existingPatient = patientRepository.findByEmail(patient.getEmail());
            return existingPatient == null; // Return true if patient doesn't exist (valid for registration)
            
        } catch (Exception e) {
            return false; // In case of error, consider as invalid
        }
    }

    // 8. validatePatientLogin Method
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find patient by email
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(401).body(response);
            }

            // Verify password (in real application, use password encoder)
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }

            // Generate token
            String token = tokenService.generateToken(patient.getEmail(), "patient");
            response.put("token", token);
            response.put("message", "Patient login successful");
            response.put("patientId", patient.getId().toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error during patient validation");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 9. filterPatient Method
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract email from token to identify patient
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

            Long patientId = patient.getId();
            
            // Apply filters based on provided parameters
            if (condition != null && name != null) {
                // Filter by both condition and doctor name
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            } else if (condition != null) {
                // Filter by condition only
                return patientService.filterByCondition(condition, patientId);
            } else if (name != null) {
                // Filter by doctor name only
                return patientService.filterByDoctor(name, patientId);
            } else {
                // No filters - return all appointments
                return patientService.getPatientAppointment(patientId, token);
            }
            
        } catch (Exception e) {
            response.put("error", "Error filtering patient appointments");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Validate doctor login
    public ResponseEntity<Map<String, String>> validateDoctorLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Find doctor by email
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            if (doctor == null) {
                response.put("error", "Doctor not found");
                return ResponseEntity.status(401).body(response);
            }

            // Verify password
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }

            // Generate token
            String token = tokenService.generateToken(doctor.getEmail(), "doctor");
            response.put("token", token);
            response.put("message", "Doctor login successful");
            response.put("doctorId", doctor.getId().toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error during doctor validation");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Generic token validation
    public Map<String, String> validateToken(String token) {
        Map<String, String> response = new HashMap<>();
        
        try {
            boolean isValid = tokenService.validateToken(token);
            if (isValid) {
                response.put("status", "valid");
                response.put("message", "Token is valid");
            } else {
                response.put("status", "invalid");
                response.put("message", "Token is invalid or expired");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error validating token");
        }
        
        return response;
    }

    // Add the missing extractEmail method that other services are calling
    public String extractEmail(String token) {
        return tokenService.extractEmail(token);
    }
}