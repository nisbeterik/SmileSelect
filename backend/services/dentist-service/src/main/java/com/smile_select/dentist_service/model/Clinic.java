package com.smile_select.dentist_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clinic")
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

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

    // No-argument constructor
    public Clinic() {
    }

    // All-argument constructor
    public Clinic(long id, String name, double longitude, double latitude, String street, int zip, String city, String houseNumber) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.houseNumber = houseNumber;
    }

    // Getters and setters

    // id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
