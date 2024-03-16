package com.example.demo.model.vo.common;

import java.util.List;

// this clazz should be given by the external system through a shared jar
// All external object must be uncoupled from internal datalayer

public class ExternalCustomer {
    private ExternalAddress address;
    private String name;
    private String preferredStore;
    private List<ExternalShoppingList> shoppingLists;
    private String externalId;
    private String companyNumber;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public boolean isCompany() {
        return companyNumber != null;
    }

    public ExternalAddress getPostalAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public void setPreferredStore(String preferredStore) {
        this.preferredStore = preferredStore;
    }

    public List<ExternalShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ExternalShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public void setAddress(ExternalAddress address) {
        this.address = address;
    }

}
