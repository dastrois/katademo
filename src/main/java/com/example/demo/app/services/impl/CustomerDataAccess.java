package com.example.demo.app.services.impl;


import com.example.demo.app.services.ICustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalShoppingItem;
import com.example.demo.repository.ICustomerDataLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccess implements ICustomerDataAccess {

    public static final String ExternalId = "ExternalId";
    public static final String CompanyNumber = "CompanyNumber";

    private static final Logger log = LoggerFactory.getLogger(CustomerDataAccess.class);

    @Autowired
    private ICustomerDataLayer customerDataLayer;

    @Override
    public CustomerMatches loadCustomer(String externalId, String companyNumber) {
        log.debug("load the customer with externalId: {} and companyId: {}", externalId, companyNumber);

        CustomerMatches matches = new CustomerMatches();

        Customer customer;

        customer = this.customerDataLayer.findByExternalId(externalId);
        if (customer == null) {
            customer = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (customer == null) {
                log.debug("nothing founded");
                return matches;
            } else {
                if (!CustomerType.COMPANY.equals(customer.getCustomerType()))
                    throw new ConflictException("Existing customer for companyNumber " + companyNumber + " already exists and is not a company");

                log.debug("founded by company number");
                matches.setMatchTerm(CompanyNumber);
            }
        } else {
            if (!CustomerType.COMPANY.equals(customer.getCustomerType()))
                throw new ConflictException("Existing customer for externalId " + externalId + " already exists and is not a company");

            log.debug("founded by extId");
            matches.setMatchTerm(ExternalId);

        }

        matches.setCustomer(customer);

        switch (matches.getMatchTerm()) {
            case CompanyNumber:
                if (!customer.getExternalId().equals(externalId)){
                    throw new ConflictException("");
                }
                break;
            case ExternalId:
                if (!customer.getCompanyNumber().equals(companyNumber)) {
                    throw new ConflictException("");
                }
                break;
        }

        matches.addDuplicate(this.customerDataLayer.findByMasterExternalId(externalId));

//
//
//        if (matchByExternalId != null) {
//            matches.setCustomer(matchByExternalId);
//            matches.setMatchTerm("ExternalId");
//            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
//            if (matchByMasterId != null) matches.addDuplicate(matchByMasterId);
//        } else {
//            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
//            if (matchByCompanyNumber != null) {
//                matches.setCustomer(matchByCompanyNumber);
//                matches.setMatchTerm("CompanyNumber");
//            }
//        }

        return matches;
    }

    @Override
    public CustomerMatches loadCustomer(String externalId) {
        log.debug("load the customer with externalId: {}", externalId);

        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);

        if (matchByPersonalNumber == null)
            return matches;

        if (!CustomerType.PERSON.equals(matchByPersonalNumber.getCustomerType()))
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");

        matches.setCustomer(matchByPersonalNumber);
        matches.setMatchTerm(ExternalId);


        return matches;
    }

    @Override
    public Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.save(customer);
    }

    @Override
    public Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.save(customer);
    }

    @Override
    public void updateShoppingList(Customer customer, ExternalShoppingItem consumerShoppingList) {


//        customer.addShoppingList(mapper.map(consumerShoppingList, ShoppingList.class));
//        shoppingListDataLayer.updateShoppingList(mapper.map(consumerShoppingList, ShoppingList.class));
        customerDataLayer.save(customer);
    }
}
