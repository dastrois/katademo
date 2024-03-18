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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@Validated
public class CustomerSync implements com.example.demo.app.ICustomerSync {

    @Autowired
    private CustomerDataAccess customerDataAccess;

    @Override
    public boolean syncWithDataLayer(@NotNull ExternalCustomer externalCustomer) {

        CustomerMatches customerMatches;
        if (externalCustomer.isCompany()) {
            customerMatches = loadPerson(externalCustomer, CustomerType.COMPANY);
            if (customerMatches == null)
                customerMatches = loadCompany(externalCustomer);
        } else {
            customerMatches = loadPerson(externalCustomer, CustomerType.PERSON);
        }

        Customer customer = customerMatches.getCustomer();

        if (customer == null) {
            customer = new Customer();
            customer.setExternalId(externalCustomer.getExternalId());
            customer.setMasterExternalId(externalCustomer.getExternalId());
        }

        populateFields(externalCustomer, customer);

        boolean created = false;
        if (customer.getInternalId() == null) {
            customer = createCustomer(customer);
            created = true;
        } else {
            updateCustomer(customer);
        }
        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(externalCustomer, duplicate);
            }
        }

        updateRelations(externalCustomer, customer);

        return created;
    }

    private void updateRelations(ExternalCustomer externalCustomer, Customer customer) {
        List<ShoppingList> sl = new ArrayList<>();
        for (ExternalShoppingItem consumerShoppingItem : externalCustomer.getShoppingLists()) {
            ShoppingList si = new ShoppingList();
            si.setCustomer(customer);
            si.setProducts(consumerShoppingItem.getProducts());
            sl.add(si);
        }
        customer.setShoppingLists(sl);
        customerDataAccess.updateCustomerRecord(customer);
    }

    private Customer updateCustomer(Customer customer) {
        return this.customerDataAccess.updateCustomerRecord(customer);
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = new Customer();
            duplicate.setExternalId(externalCustomer.getExternalId());
            duplicate.setMasterExternalId(externalCustomer.getExternalId());
        }

        duplicate.setName(externalCustomer.getName());

        if (duplicate.getInternalId() == null) {
            createCustomer(duplicate);
        } else {
            updateCustomer(duplicate);
        }
    }

    private Customer createCustomer(Customer customer) {
        return this.customerDataAccess.createCustomerRecord(customer);
    }

    /**
     * Map all fields from external --> Customer
     */
    private void populateFields(ExternalCustomer externalCustomer, Customer customer) {
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
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        CustomerMatches customerMatches = customerDataAccess.loadCustomerCompany(externalId, companyNumber);

        return customerMatches;
    }

    private CustomerMatches loadPerson(ExternalCustomer externalCustomer, CustomerType type) throws ConflictException {
            return customerDataAccess.loadCustomer(externalCustomer.getExternalId(), type, type == CustomerType.COMPANY ? externalCustomer.getCompanyNumber() : null);
    }
}
