package com.smile_select.patient_service.dto;

public class PatientFirstNameAndEmailDTO {

    private String firstName;
    private String email;

    public PatientFirstNameAndEmailDTO(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
