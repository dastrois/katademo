package com.example.demo.app.services;


import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalShoppingItem;

public interface ICustomerDataAccess {
    CustomerMatches loadCustomerCompany(String externalId,String companyNumber) throws ConflictException;

    CustomerMatches loadCustomer(String externalId, CustomerType type) throws ConflictException;

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    void updateShoppingList(Customer customer, ExternalShoppingItem consumerShoppingList);
}
