package com.smile_select.patient_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "role")
    private String role = "PATIENT";

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientPreferredDate> preferredDates;

    // No-argument constructor
    public Patient() {
    }

    // All-argument constructor
    public Patient(Long id, String firstName, String lastName, String email, String password, LocalDate dateOfBirth, String role, List<PatientPreferredDate> preferredDates) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.preferredDates = preferredDates;
    }

    // Getters and setters

    // id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // firstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // dateOfBirth
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<PatientPreferredDate> getPreferredDates() {
        return preferredDates;
    }

    public void setPreferredDates(List<PatientPreferredDate> preferredDates) {
        this.preferredDates = preferredDates;
    }

    
}
