package com.example.demo.model.dao;

import com.example.demo.model.constant.CustomerType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("serial")

@Entity
public class Customer implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    private String internalId;
    private String externalId;
    private String masterExternalId;
    @OneToOne
    @JoinColumn(name="id")
    private Address address;
    private String preferredStore;
    @OneToMany(mappedBy = "customer")
    private List<ShoppingList> shoppingLists;
    private String name;
//    @Convert("CustomerType")
    private CustomerType customerType;
    private String companyNumber;

    private int bonusPointBalance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMasterExternalId() {
        return masterExternalId;
    }

    public void setMasterExternalId(String masterExternalId) {
        this.masterExternalId = masterExternalId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public void setPreferredStore(String preferredStore) {
        this.preferredStore = preferredStore;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public int getBonusPointBalance() {
        return bonusPointBalance;
    }

    public void setBonusPointBalance(int bonusPointBalance) {
        this.bonusPointBalance = bonusPointBalance;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public int getBonusPointBalance() {
        return bonusPointBalance;
    }

    public void setBonusPointBalance(int bonusPointBalance) {
        this.bonusPointBalance = bonusPointBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(externalId, customer.externalId) &&
                Objects.equals(masterExternalId, customer.masterExternalId) &&
                Objects.equals(companyNumber, customer.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, masterExternalId, companyNumber);
    }

    @Override
    public String toString() {
        return "Customer [" +
                internalId != null ? "internalId=" + internalId + ", " : "" +
                externalId != null ? "externalId=" + externalId + ", " : "" +
                 "]";
    }

    public Customer() {
    }

    public Customer(String externalId, String masterExternalId) {
        this.externalId = externalId;
        this.masterExternalId = masterExternalId;
    }
}
