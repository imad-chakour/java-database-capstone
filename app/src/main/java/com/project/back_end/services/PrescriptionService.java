package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PrescriptionService {

    // 2. Constructor Injection for Dependencies
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // 3. savePrescription Method
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Check if prescription already exists for this appointment
            List<Prescription> existingPrescriptions = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existingPrescriptions.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate required fields
            if (prescription.getAppointmentId() == null || 
                prescription.getPatientId() == null || 
                prescription.getDoctorId() == null ||
                prescription.getMedications() == null || 
                prescription.getMedications().isEmpty()) {
                response.put("message", "Missing required prescription fields");
                return ResponseEntity.badRequest().body(response);
            }

            // Set default status if not provided
            if (prescription.getStatus() == null || prescription.getStatus().isEmpty()) {
                prescription.setStatus("ACTIVE");
            }

            // Save the prescription
            Prescription savedPrescription = prescriptionRepository.save(prescription);
            
            if (savedPrescription.getId() != null) {
                response.put("message", "Prescription saved successfully");
                response.put("prescriptionId", savedPrescription.getId());
                return ResponseEntity.status(201).body(response);
            } else {
                response.put("message", "Failed to save prescription");
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            // Log the error (in a real application, use a proper logger)
            System.err.println("Error saving prescription: " + e.getMessage());
            response.put("message", "Internal server error while saving prescription");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 4. getPrescription Method
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate appointment ID
            if (appointmentId == null || appointmentId <= 0) {
                response.put("error", "Invalid appointment ID");
                return ResponseEntity.badRequest().body(response);
            }

            // Fetch prescriptions for the appointment
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            
            if (prescriptions.isEmpty()) {
                response.put("message", "No prescription found for this appointment");
                response.put("prescriptions", prescriptions);
                return ResponseEntity.ok(response);
            }

            response.put("prescriptions", prescriptions);
            response.put("count", prescriptions.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log the error
            System.err.println("Error retrieving prescription: " + e.getMessage());
            response.put("error", "Internal server error while retrieving prescription");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Get prescription by ID
    public ResponseEntity<Map<String, Object>> getPrescriptionById(String prescriptionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                response.put("error", "Invalid prescription ID");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Prescription> prescription = prescriptionRepository.findById(prescriptionId);
            
            if (prescription.isPresent()) {
                response.put("prescription", prescription.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Prescription not found");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error retrieving prescription by ID: " + e.getMessage());
            response.put("error", "Internal server error while retrieving prescription");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Get prescriptions by patient ID
    public ResponseEntity<Map<String, Object>> getPrescriptionsByPatient(Long patientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (patientId == null || patientId <= 0) {
                response.put("error", "Invalid patient ID");
                return ResponseEntity.badRequest().body(response);
            }

            List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
            
            response.put("prescriptions", prescriptions);
            response.put("count", prescriptions.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error retrieving patient prescriptions: " + e.getMessage());
            response.put("error", "Internal server error while retrieving prescriptions");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Get prescriptions by doctor ID
    public ResponseEntity<Map<String, Object>> getPrescriptionsByDoctor(Long doctorId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (doctorId == null || doctorId <= 0) {
                response.put("error", "Invalid doctor ID");
                return ResponseEntity.badRequest().body(response);
            }

            List<Prescription> prescriptions = prescriptionRepository.findByDoctorId(doctorId);
            
            response.put("prescriptions", prescriptions);
            response.put("count", prescriptions.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error retrieving doctor prescriptions: " + e.getMessage());
            response.put("error", "Internal server error while retrieving prescriptions");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Update prescription status
    public ResponseEntity<Map<String, String>> updatePrescriptionStatus(String prescriptionId, String status) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                response.put("error", "Invalid prescription ID");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(prescriptionId);
            
            if (prescriptionOpt.isPresent()) {
                Prescription prescription = prescriptionOpt.get();
                prescription.setStatus(status);
                prescriptionRepository.save(prescription);
                
                response.put("message", "Prescription status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Prescription not found");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error updating prescription status: " + e.getMessage());
            response.put("error", "Internal server error while updating prescription");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Additional method: Delete prescription
    public ResponseEntity<Map<String, String>> deletePrescription(String prescriptionId) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (prescriptionId == null || prescriptionId.isEmpty()) {
                response.put("error", "Invalid prescription ID");
                return ResponseEntity.badRequest().body(response);
            }

            if (prescriptionRepository.existsById(prescriptionId)) {
                prescriptionRepository.deleteById(prescriptionId);
                response.put("message", "Prescription deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Prescription not found");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error deleting prescription: " + e.getMessage());
            response.put("error", "Internal server error while deleting prescription");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}