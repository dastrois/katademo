package com.example.demo.repository;


import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICustomerDataLayer extends JpaRepository<Customer, Long> {

//    Customer updateCustomerRecord(Customer customer);
//
//    Customer createCustomerRecord(Customer customer);



    Customer findByExternalId(String externalId);

    Customer findByExternalIdAndCustomerType(String externalId, CustomerType type);

    List<Customer> findByMasterExternalId(String externalId);

    Customer findByCompanyNumber(String companyNumber);
}
