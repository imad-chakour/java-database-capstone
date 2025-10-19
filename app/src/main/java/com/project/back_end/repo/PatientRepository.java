package com.project.back_end.repo;

import com.project.back_end.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 2. Custom Query Methods:

    /**
     * Find a patient by their email address
     * @param email the email to search for
     * @return Patient entity matching the email, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Find a patient using either email or phone number
     * @param email the email to search for
     * @param phone the phone number to search for
     * @return Patient entity matching either email or phone, or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);
}