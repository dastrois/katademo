package com.example.demo.app.services.impl;

import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.repository.ICustomerDataLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.Assert;

class CustomerDataAccessTest {

    @Mock
    ICustomerDataLayer customerDataLayer;
    @InjectMocks
    private CustomerDataAccess testObject;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadCompanyCustomerNoDuplicate() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByExternalId(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = testObject.loadCompanyCustomer("vv", "gg");

        Assert.notNull(cm, "");
        Assert.isTrue( cm.getDuplicates().size() == 0, "");
    }
    @Test
    void testLoadCompanyCustomerWithDuplicate() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByExternalId(ArgumentMatchers.anyString())).thenReturn(cus);
        Mockito.when(customerDataLayer.findByMasterExternalId(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = testObject.loadCompanyCustomer("vv", "gg");

        Assert.notNull(cm, "");
        Assert.isTrue( cm.hasDuplicates(), "");

        Assert.isTrue(cm.getDuplicates().iterator().next().getCompanyNumber() == cus.getCompanyNumber(), "");

    }
    @Test
    void testLoadCompanyCustomerByCompanyName() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByCompanyNumber(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = testObject.loadCompanyCustomer("vv", "gg");

        Assert.notNull(cm, "");
        Assert.isTrue( ! cm.hasDuplicates(), "");
        Assert.isTrue(cm.getCustomer().getCompanyNumber() == cus.getCompanyNumber(), "");
    }

    @Test
    public void testLoadPersonCustomer(){
        Customer cus = new Customer();
        cus.setCustomerType(CustomerType.PERSON);
        cus.setName("ert");
        cus.setExternalId("jko");

        Mockito.when(customerDataLayer.findByExternalId(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = testObject.loadPersonCustomer("ddd");
        Assert.notNull(cm, "not null");
        Assert.notNull(cm.getCustomer(), "customer not null");

        Assert.isTrue(cm.getCustomer().getName() == cus.getName(), "customer name");

    }
}