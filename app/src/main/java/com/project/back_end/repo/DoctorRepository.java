package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 2. Custom Query Methods:

    /**
     * Find a doctor by their email address
     * @param email the email to search for
     * @return Doctor entity matching the email, or null if not found
     */
    Doctor findByEmail(String email);

    /**
     * Find doctors by partial name match using LIKE pattern
     * @param name partial name to search for
     * @return List of doctors whose names contain the given string
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Filter doctors by partial name and exact specialty (case-insensitive)
     * @param name partial name to search for (case-insensitive)
     * @param specialty exact specialty to match (case-insensitive)
     * @return List of doctors matching both criteria
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(@Param("name") String name, 
                                                                     @Param("specialty") String specialty);

    /**
     * Find doctors by specialty, ignoring case
     * @param specialty the specialty to search for
     * @return List of doctors with the specified specialty
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}