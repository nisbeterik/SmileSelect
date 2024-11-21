package com.smile_select.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Data Transfer Object (DTO) for transferring Dentist entity data.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DentistDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long clinicId;

    private double longitude;      // Longitude of the clinic
    private double latitude;       // Latitude of the clinic
    private String street;         // Street of the clinic
    private int zip;               // ZIP code of the clinic
    private String city;           // City of the clinic
    private String houseNumber;    // House number of the clinic
}
