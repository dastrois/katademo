package com.example.demo.repository;


import com.example.demo.model.dao.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface ICustomerDataLayer extends JpaRepository<Customer, Long> {

//    Customer updateCustomerRecord(Customer customer);
//
//    Customer createCustomerRecord(Customer customer);



    Customer findByExternalId(String externalId);

    Customer findByMasterExternalId(String externalId);

    Customer findByCompanyNumber(String companyNumber);
}
