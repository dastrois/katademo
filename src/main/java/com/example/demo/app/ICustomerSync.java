package com.example.demo.app;

import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalCustomer;

public interface ICustomerSync {
    //    public CustomerSync(CustomerDataAccess db) {
    //        this.customerDataAccess = db;
    //    }
    //
    boolean syncWithDataLayer(ExternalCustomer externalCustomer);

    CustomerMatches loadCompany(ExternalCustomer externalCustomer);

    CustomerMatches loadPerson(ExternalCustomer externalCustomer);
}
