package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Patient name is required")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;
    
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Medications are required")
    @Size(min = 3, max = 500, message = "Medications must be between 3 and 500 characters")
    private String medications;

    @NotNull(message = "Dosage is required")
    @Size(min = 3, max = 100, message = "Dosage must be between 3 and 100 characters")
    private String dosage;

    @Size(max = 500, message = "Doctor notes must not exceed 500 characters")
    private String doctorNotes;

    private String status;

    // Default constructor
    public Prescription() {
    }

    // Parameterized constructor
    public Prescription(Long patientId, Long doctorId, String patientName, Long appointmentId, 
                       String medications, String dosage, String doctorNotes, String status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medications = medications;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
        this.status = status;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // ADDED MISSING METHODS that your code is calling:
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    // CHANGED from getMedication() to getMedications() to match what your code expects
    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    // ADDED MISSING STATUS METHODS that your code is calling:
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Utility methods
    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", patientName='" + patientName + '\'' +
                ", appointmentId=" + appointmentId +
                ", medications='" + medications + '\'' +
                ", dosage='" + dosage + '\'' +
                ", doctorNotes='" + (doctorNotes != null ? doctorNotes : "None") + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Helper method to check if prescription has doctor notes
    public boolean hasDoctorNotes() {
        return doctorNotes != null && !doctorNotes.trim().isEmpty();
    }

    // Helper method to get prescription summary
    public String getPrescriptionSummary() {
        return String.format("%s - %s for %s", medications, dosage, patientName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}