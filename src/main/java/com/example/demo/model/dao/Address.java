package com.example.demo.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;

@SuppressWarnings("serial")

@Entity
public class Address implements Serializable {
    @Id
    private Long id;
    private String street;
    private String city;
    private String postalCode;
    public Address(String street, String city, String postalCode) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
    }

    public Address() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address [" +
                street != null ? "street=" + street + ", " : "" +
                city != null ? "city=" + city + ", " : "" +
                "]";
    }
}
