package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    // 2. Constructor Injection for Dependencies
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                        AppointmentRepository appointmentRepository,
                        TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. getDoctorAvailability Method
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return Collections.emptyList();
            }

            Doctor doctor = doctorOpt.get();
            List<String> availableSlots = new ArrayList<>();

            // Get doctor's available times
            List<String> availableTimes = doctor.getAvailableTimes();
            if (availableTimes == null || availableTimes.isEmpty()) {
                return Collections.emptyList();
            }

            // Get booked appointments for the date
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

            // Extract booked time slots
            Set<LocalTime> bookedSlots = appointments.stream()
                .map(appt -> appt.getAppointmentTime().toLocalTime())
                .collect(Collectors.toSet());

            // Filter available slots
            for (String timeSlot : availableTimes) {
                try {
                    LocalTime slotTime = LocalTime.parse(timeSlot);
                    if (!bookedSlots.contains(slotTime)) {
                        availableSlots.add(timeSlot);
                    }
                } catch (Exception e) {
                    // Skip invalid time formats
                }
            }

            return availableSlots.stream()
                .sorted()
                .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 5. saveDoctor Method
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            // Check if doctor already exists by email
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1; // Doctor already exists
            }

            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    // 6. updateDoctor Method
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctorOpt = doctorRepository.findById(doctor.getId());
            if (existingDoctorOpt.isEmpty()) {
                return -1; // Doctor not found
            }

            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    // 7. getDoctors Method
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 8. deleteDoctor Method
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (doctorOpt.isEmpty()) {
                return -1; // Doctor not found
            }

            // Delete associated appointments
            appointmentRepository.deleteAllByDoctorId(id);
            
            // Delete the doctor
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    // 9. validateDoctor Method
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            if (doctor == null) {
                response.put("message", "Doctor not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify password (in real application, use password encoder)
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.badRequest().body(response);
            }

            // Generate token
            String token = tokenService.generateDoctorToken(doctor.getId(), doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 10. findDoctorByName Method
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
        } catch (Exception e) {
            response.put("error", "Error searching doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 11. filterDoctorsByNameSpecilityandTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 12. filterDoctorByTime Method (Private helper)
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
    if (doctors == null || doctors.isEmpty() || amOrPm == null) {
        return doctors;
    }

    return doctors.stream()
        .filter(doctor -> {
            List<String> availableTimes = doctor.getAvailableTimes();
            if (availableTimes == null || availableTimes.isEmpty()) {
                return false;
            }

            return availableTimes.stream()
                .anyMatch(timeSlot -> {
                    try {
                        LocalTime time = LocalTime.parse(timeSlot);
                        if ("AM".equalsIgnoreCase(amOrPm)) {
                            return time.isBefore(LocalTime.NOON);
                        } else if ("PM".equalsIgnoreCase(amOrPm)) {
                            return !time.isBefore(LocalTime.NOON);
                        }
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
        })
        .collect(Collectors.toList());
}

    // 13. filterDoctorByNameAndTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 14. filterDoctorByNameAndSpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 15. filterDoctorByTimeAndSpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 16. filterDoctorBySpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            response.put("doctors", doctors);
            response.put("count", doctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }

    // 17. filterDoctorsByTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Doctor> allDoctors = doctorRepository.findAll();
            List<Doctor> filteredDoctors = filterDoctorByTime(allDoctors, amOrPm);
            
            response.put("doctors", filteredDoctors);
            response.put("count", filteredDoctors.size());
        } catch (Exception e) {
            response.put("error", "Error filtering doctors");
            response.put("doctors", Collections.emptyList());
            response.put("count", 0);
        }
        
        return response;
    }
}