package com.smile_select.account_service.repository;

import com.smile_select.account_service.model.Clinic;
import com.smile_select.account_service.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}

