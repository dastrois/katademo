package com.example.demo.app.services;


import com.example.demo.model.dao.Customer;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalShoppingItem;

public interface ICustomerDataAccess {
    CustomerMatches loadCompanyCustomer(String externalId, String companyNumber);

    CustomerMatches loadPersonCustomer(String externalId);

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    void updateShoppingList(Customer customer, ExternalShoppingItem consumerShoppingList);
}
