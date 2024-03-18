package com.example.demo.app.services.impl;


import com.example.demo.app.services.ICustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
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
    public CustomerMatches loadCustomerCompany(String externalId, String companyNumber) {
        log.debug("load the customer with companyId: {}", companyNumber);

        CustomerMatches matches = new CustomerMatches();

        Customer customer;

//        customer = this.customerDataLayer.findByExternalId(externalId);
//        if (customer == null) {
        customer = this.customerDataLayer.findByCompanyNumber(companyNumber);
        if (customer == null) {
            log.debug("nothing founded");
            return matches;
        }

        // Verify the right customer
        if (!CustomerType.COMPANY.equals(customer.getCustomerType()))
            throw new ConflictException("Existing customer for companyNumber " + companyNumber + " already exists and is not a company");

        if (!customer.getExternalId().equals(externalId)) {
            throw new ConflictException("Existing customer for companyNumber " + companyNumber + " already exists with different externalId");
        }


        log.debug("founded by company number");
        matches.setMatchTerm(CompanyNumber);

        matches.setCustomer(customer);

        matches.addDuplicate(this.customerDataLayer.findByMasterExternalId(externalId));

        return matches;
    }

    @Override
    public CustomerMatches loadCustomer(String externalId, CustomerType type, String companyNumber) throws ConflictException {
        log.debug("load the customer with externalId: {}", externalId);

        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalIdAndCustomerType(externalId, type);

        if (matchByPersonalNumber == null){
            log.debug("nothing founded");
            return matches;
        }

        if (type == CustomerType.COMPANY){
            // chack cmpy Id
            if (matchByPersonalNumber.getCompanyNumber() != companyNumber)
                throw new ConflictException("Existing customer for externalNumber " + externalId + " already exists with different companyNumber");
        }

        matches.setCustomer(matchByPersonalNumber);
        matches.setMatchTerm(ExternalId);

        matches.addDuplicate(this.customerDataLayer.findByMasterExternalId(externalId));

        return matches;
    }

    @Override
    public Customer upSaveCustomer(Customer customer) {
        return customerDataLayer.save(customer);
    }
}
