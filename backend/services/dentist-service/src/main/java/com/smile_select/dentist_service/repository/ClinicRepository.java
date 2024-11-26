package com.smile_select.dentist_service.repository;

import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}

