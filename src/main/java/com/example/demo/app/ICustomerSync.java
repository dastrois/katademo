package com.example.demo.app;

import com.example.demo.model.vo.common.ExternalCustomer;

public interface ICustomerSync {
    boolean syncWithDataLayer(ExternalCustomer externalCustomer);
}
