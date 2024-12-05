package com.smile_select.patient_service.repository;

import com.smile_select.patient_service.model.PatientPreferredDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientPreferredDateRepository extends JpaRepository<PatientPreferredDate, Long>,JpaSpecificationExecutor<PatientPreferredDate> {
} 