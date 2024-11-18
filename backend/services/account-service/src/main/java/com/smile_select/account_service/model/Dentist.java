package com.smile_select.account_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dentist")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "zip", nullable = false)
    private int zip;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

}
