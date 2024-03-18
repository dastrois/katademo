package com.example.demo.model.vo;


import com.example.demo.model.dao.Customer;

import java.util.ArrayList;
import java.util.Collection;

public class CustomerMatches {
    private Collection<Customer> duplicates = new ArrayList<>();
    private String matchTerm;
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    public void addDuplicate(Customer duplicate) {
        if (duplicate != null)
            duplicates.add(duplicate);
    }

    public Collection<Customer> getDuplicates() {
        return duplicates;
    }

    public String getMatchTerm() {
        return matchTerm;
    }

    public void setMatchTerm(String matchTerm) {
        this.matchTerm = matchTerm;
    }
}
