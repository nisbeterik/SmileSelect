package com.smile_select.patient_service.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_preferred_date")
public class PatientPreferredDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private LocalDate patientId;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    public PatientPreferredDate() {
    }
    

    public PatientPreferredDate(Long id, LocalDate patientId, LocalDate preferredDate) {
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

    public LocalDate getPatientId() {
        return patientId;
    }

    public void setPatientId(LocalDate patientId) {
        this.patientId = patientId;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

}
