package com.project.back_end.repo;

import com.project.back_end.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 2. Custom Query Method:
    /**
     * Find an Admin by their username
     * @param username the username to search for
     * @return Admin entity matching the username, or null if not found
     */
    Admin findByUsername(String username);
}