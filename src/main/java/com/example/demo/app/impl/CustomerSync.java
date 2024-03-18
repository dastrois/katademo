package com.example.demo.app.impl;

import com.example.demo.app.services.impl.CustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Address;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.dao.ShoppingList;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalCustomer;
import com.example.demo.model.vo.common.ExternalShoppingItem;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@Validated
public class CustomerSync implements com.example.demo.app.ICustomerSync {

    private static final Logger log = LoggerFactory.getLogger(CustomerSync.class);

    @Autowired
    private CustomerDataAccess customerDataAccess;

    @Override
    public boolean syncWithDataLayer(@NotNull ExternalCustomer externalCustomer) {
        log.debug("sync externalCustomer: {}", externalCustomer);

        // Get customer Matches from db
        CustomerMatches customerMatches = loadPerson(externalCustomer, externalCustomer.isCompany() ? CustomerType.COMPANY : CustomerType.PERSON);
        if (customerMatches == null && externalCustomer.isCompany())
            customerMatches = loadCompany(externalCustomer);

        // retrieve the founded customer or create a new one
        Customer customer = (customerMatches != null && customerMatches.getCustomer() != null) ? customerMatches.getCustomer() : new Customer(externalCustomer.getExternalId(), externalCustomer.getExternalId());
        populateFields(externalCustomer, customer);
        boolean created = customer.getInternalId() == null;

        populateShoppingList(externalCustomer, customer);

        // save the customer before handling duplicate
        updateCustomer(customer);

        // handle duplicate
        if (customerMatches != null && customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(externalCustomer, duplicate);
            }
        }
        return created;
    }

    private void populateShoppingList(ExternalCustomer externalCustomer, Customer customer) {
        log.debug("populate the shopping list");
        List<ShoppingList> sl = new ArrayList<>();
        for (ExternalShoppingItem consumerShoppingItem : externalCustomer.getShoppingLists()) {
            ShoppingList si = new ShoppingList();
            si.setCustomer(customer);
            si.setProducts(consumerShoppingItem.getProducts());
            sl.add(si);
        }
        customer.setShoppingLists(sl);
    }

    private Customer updateCustomer(Customer customer) {
        return this.customerDataAccess.upSaveCustomer(customer);
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = new Customer();
            duplicate.setExternalId(externalCustomer.getExternalId());
            duplicate.setMasterExternalId(externalCustomer.getExternalId());
        }

        duplicate.setName(externalCustomer.getName());

        updateCustomer(duplicate);
    }

    /**
     * Map all fields from external --> Customer
     */
    private void populateFields(ExternalCustomer externalCustomer, Customer customer) {
        log.debug("map the fields from external to internal");
        customer.setName(externalCustomer.getName());
        customer.setPreferredStore(externalCustomer.getPreferredStore());
        if (externalCustomer.isCompany()) {
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setCustomerType(CustomerType.PERSON);
            if (customer.getBonusPointBalance() != externalCustomer.getBonusPointBalance())
                customer.setBonusPointBalance(externalCustomer.getBonusPointBalance());
        }
        updateContactInfo(externalCustomer, customer);
    }

    private void updateContactInfo(ExternalCustomer externalCustomer, Customer customer) {

        //TODO should use orika

        Address adr = new Address();
        adr.setCity(externalCustomer.getPostalAddress().getCity());
        adr.setStreet(externalCustomer.getPostalAddress().getStreet());
        adr.setPostalCode(externalCustomer.getPostalAddress().getPostalCode());

        customer.setAddress(adr);
    }

    private CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        log.debug("load the company for extCust: {}", externalCustomer);

        return customerDataAccess.loadCustomerCompany(externalCustomer.getExternalId(), externalCustomer.getCompanyNumber());
    }

    private CustomerMatches loadPerson(ExternalCustomer externalCustomer, CustomerType type) throws ConflictException {
        log.debug("load the person for extCust: {}", externalCustomer);

        return customerDataAccess.loadCustomer(externalCustomer.getExternalId(), type, type == CustomerType.COMPANY ? externalCustomer.getCompanyNumber() : null);
    }
}
