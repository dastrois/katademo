package com.example.demo.app.services;

import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;

public interface ICustomerDataAccess {
    CustomerMatches loadCustomerCompany(String externalId, String companyNumber) throws ConflictException;

    CustomerMatches loadCustomer(String externalId, CustomerType type, String companyNumber) throws ConflictException;

    Customer upSaveCustomer(Customer customer);


}
