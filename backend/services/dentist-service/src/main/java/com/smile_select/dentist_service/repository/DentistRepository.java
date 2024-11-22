package com.smile_select.dentist_service.repository;

import com.smile_select.account_service.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Dentist entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {
    Optional<Dentist> findByEmail(String email);
}