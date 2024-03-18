package com.example.demo.model.vo;


import com.example.demo.model.constant.MatchTerm;
import com.example.demo.model.dao.Customer;

import java.util.ArrayList;
import java.util.Collection;

public class CustomerMatches {
    private Collection<Customer> duplicates = new ArrayList<>();
    private MatchTerm matchTerm;
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

    public MatchTerm getMatchTerm() {
        return matchTerm;
    }

    public void setMatchTerm(MatchTerm matchTerm) {
        this.matchTerm = matchTerm;
    }
}
