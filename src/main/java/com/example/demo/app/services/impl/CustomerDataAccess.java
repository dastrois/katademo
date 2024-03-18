package com.example.demo.app.services.impl;


import com.example.demo.app.services.ICustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.constant.MatchTerm;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.repository.ICustomerDataLayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccess implements ICustomerDataAccess {

    private static final Logger log = LoggerFactory.getLogger(CustomerDataAccess.class);

    @Autowired
    private ICustomerDataLayer customerDataLayer;

    @Override
    public CustomerMatches loadCustomerCompany(@NotNull String externalId, @NotNull String companyNumber) {
        log.debug("load the customer with companyId: {}", companyNumber);

        Customer customer = this.customerDataLayer.findByCompanyNumber(companyNumber);
        if (customer == null) {
            log.debug("nothing founded");
            return new CustomerMatches();
        }

        // Verify the right customer
        if (!CustomerType.COMPANY.equals(customer.getCustomerType()))
            throw new ConflictException("Existing customer for companyNumber " + companyNumber + " already exists and is not a company");

        if (!customer.getExternalId().equals(externalId)) {
            throw new ConflictException("Existing customer for companyNumber " + companyNumber + " already exists with different externalId");
        }

        return completeResponse(customer, MatchTerm.COMPANYNUMBER);
    }

    @Override
    public CustomerMatches loadCustomer(@NotNull String externalId, @NotNull CustomerType type, String companyNumber) throws ConflictException {
        log.debug("load the customer with externalId: {}", externalId);

        Customer customer = this.customerDataLayer.findByExternalIdAndCustomerType(externalId, type);

        if (customer == null){
            log.debug("nothing founded");
            return new CustomerMatches();
        }

        if (type == CustomerType.COMPANY){
            // check cmpy Id
            if (customer.getCompanyNumber() != companyNumber)
                throw new ConflictException("Existing customer for externalNumber " + externalId + " already exists with different companyNumber");
        }

        return completeResponse(customer, MatchTerm.ExTERNALID);
    }

    private @NotNull CustomerMatches completeResponse(Customer customer, MatchTerm matchTerm){
        log.debug("complete the response for customer: {}", customer);

        CustomerMatches matches = new CustomerMatches();
        matches.setMatchTerm(matchTerm);
        matches.setCustomer(customer);
        matches.addDuplicate(this.customerDataLayer.findByMasterExternalId(customer.getExternalId()));

        return matches;
    }

    @Override
    public Customer upSaveCustomer(@NotNull Customer customer) {
        return customerDataLayer.save(customer);
    }
}
