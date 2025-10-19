package com.project.back_end.repo;

import com.project.back_end.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // 2. Custom Query Method:

    /**
     * Find prescriptions associated with a specific appointment
     * @param appointmentId the appointment ID to search for
     * @return List of prescriptions for the given appointment
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}