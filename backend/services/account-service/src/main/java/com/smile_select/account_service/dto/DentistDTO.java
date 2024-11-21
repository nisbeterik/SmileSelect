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
}
