package com.smile_select.patient_service.repository;

import com.smile_select.patient_service.dto.PatientFirstNameAndEmailDTO;
import com.smile_select.patient_service.model.PatientPreferredDate;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientPreferredDateRepository
        extends JpaRepository<PatientPreferredDate, Long>, JpaSpecificationExecutor<PatientPreferredDate> {

    @Query("SELECT p.email FROM Patient p JOIN p.preferredDates pd WHERE pd.preferredDate = :preferredDate")
    List<String> findEmailsByPreferredDate(@Param("preferredDate") LocalDate preferredDate);

    @Query("SELECT new com.smile_select.patient_service.dto.PatientFirstNameAndEmailDTO(p.firstName, p.email) " +
            "FROM Patient p JOIN p.preferredDates pd WHERE pd.preferredDate = :preferredDate")
    List<PatientFirstNameAndEmailDTO> findEmailsAndFirstNamesByPreferredDate(@Param("preferredDate") LocalDate preferredDate);
}