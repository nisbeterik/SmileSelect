package com.smile_select.patient_service.dto;

import java.time.LocalDate;

public class PatientPreferredDateDTO {

    private Long id;
    private Long patientId;
    private LocalDate preferredDate;

    public PatientPreferredDateDTO(Long id, Long patientId, LocalDate preferredDate) {
        this.id = id;
        this.patientId = patientId;
        this.preferredDate = preferredDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    

}