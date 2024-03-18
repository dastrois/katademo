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
            customerMatches = loadCompany(externalCustomer);
        } else {
            customerMatches = loadPerson(externalCustomer);
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
//        updateContactInfo(externalCustomer, customer);

        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(externalCustomer, duplicate);
            }
        }

        updateRelations(externalCustomer, customer);
//        updatePreferredStore(externalCustomer, customer);

        return created;
    }

    private void updateRelations(ExternalCustomer externalCustomer, Customer customer) {
        List<ShoppingList> sl = new ArrayList<>();
        for (ExternalShoppingItem consumerShoppingItem : externalCustomer.getShoppingLists()) {
            ShoppingList si = new ShoppingList();
            si.setCustomer(customer);
            si.setProducts(consumerShoppingItem.getProducts());
            sl.add(si);
//            this.customerDataAccess.updateShoppingList(customer, consumerShoppingItem);
        }
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
//        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
//        MapperFacade mapper = mapperFactory.getMapperFacade();
//
//        customer.setAddress(mapper.map(externalCustomer.getPostalAddress(), Address.class));

    }

    private CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        CustomerMatches customerMatches = customerDataAccess.loadCustomer(externalId, companyNumber);

//        if (customerMatches.getCustomer() != null && !CustomerType.COMPANY.equals(customerMatches.getCustomer().getCustomerType())) {
//            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
//        }

        if ("ExternalId".equals(customerMatches.getMatchTerm())) {
            String customerCompanyNumber = customerMatches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                customerMatches.getCustomer().setMasterExternalId(null);
                customerMatches.addDuplicate(customerMatches.getCustomer());
                customerMatches.setCustomer(null);
                customerMatches.setMatchTerm(null);
            }
        } else if ("CompanyNumber".equals(customerMatches.getMatchTerm())) {
            String customerExternalId = customerMatches.getCustomer().getExternalId();
            if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
            }
            Customer customer = customerMatches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
            customerMatches.addDuplicate(null);
        }

        return customerMatches;
    }

    private CustomerMatches loadPerson(ExternalCustomer externalCustomer) {
        return customerDataAccess.loadCustomer(externalCustomer.getExternalId());
    }
}
