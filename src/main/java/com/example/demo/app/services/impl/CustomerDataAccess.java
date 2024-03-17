package com.example.demo.app.services.impl;


import com.example.demo.app.services.ICustomerDataAccess;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalShoppingItem;
import com.example.demo.repository.ICustomerDataLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccess implements ICustomerDataAccess {

    @Autowired
    private ICustomerDataLayer customerDataLayer;

    @Override
    public CustomerMatches loadCompanyCustomer(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId != null) {
            matches.setCustomer(matchByExternalId);
            matches.setMatchTerm("ExternalId");
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) matches.addDuplicate(matchByMasterId);
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm("CompanyNumber");
            }
        }

        return matches;
    }

    @Override
    public CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) matches.setMatchTerm("ExternalId");
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
