package com.smile_select.patient_service.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "patient_preferred_date")
public class PatientPreferredDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    public PatientPreferredDate() {
    }

    public PatientPreferredDate(Long id, Patient patient, LocalDate preferredDate) {
        this.id = id;
        this.patient = patient;
        this.preferredDate = preferredDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientPreferredDate that = (PatientPreferredDate) o;
        return Objects.equals(preferredDate, that.preferredDate) && Objects.equals(patient, that.patient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preferredDate, patient);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

}
