package com.smile_select.dentist_service.dto;

import jakarta.validation.constraints.*;

public class ClinicDTO {

    @NotBlank(message = "Clinic name is required")
    @Size(max = 50, message = "Clinic name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    private Double latitude;

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    @NotNull(message = "Zip code is required")
    @Min(value = 0, message = "Zip code must be a positive number")
    private Integer zip;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "House number is required")
    private String houseNumber;

    public ClinicDTO() {
    }

    public ClinicDTO(String name, Double longitude, Double latitude, String street, Integer zip, String city, String houseNumber) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.houseNumber = houseNumber;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
