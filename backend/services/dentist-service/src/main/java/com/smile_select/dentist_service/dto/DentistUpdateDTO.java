package com.smile_select.dentist_service.dto;

/**
 * A Data Transfer Object (DTO) for updating Dentist entity data.
 */
public class DentistUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Clinic details for dentist
    private Long clinicId;
    private String clinicName;

    // Additional/optional clinic details for dentist
    private double longitude;
    private double latitude;
    private String street;
    private int zip;
    private String city;
    private String houseNumber;

    // No-argument constructor
    public DentistUpdateDTO() {
    }

    // All-argument constructor
    public DentistUpdateDTO(String firstName, String lastName, String email, String password,
                            Long clinicId, String clinicName, double longitude, double latitude,
                            String street, int zip, String city, String houseNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.houseNumber = houseNumber;
    }

    // Getters and setters for all fields

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

    // clinicId
    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    // clinicName
    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    // longitude
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // latitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // street
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    // zip
    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    // city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // houseNumber
    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
