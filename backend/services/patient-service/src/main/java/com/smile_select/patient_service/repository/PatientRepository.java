package com.smile_select.patient_service.repository;

import com.smile_select.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>,JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByEmail(String email);
}