package com.example.demo.app.services.impl;

import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.constant.MatchTerm;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.repository.ICustomerDataLayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

class CustomerDataAccessTest {

    @Mock
    ICustomerDataLayer customerDataLayer;
    @InjectMocks
    private CustomerDataAccess service2Test;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadCompanyCustomerNotFound() {
        Mockito.when(customerDataLayer.findByExternalId(ArgumentMatchers.anyString())).thenReturn(null);

        CustomerMatches cm = service2Test.loadCustomerCompany("aaa", "bbb");

        Assert.notNull(cm, "");
        Assert.isTrue(cm.getCustomer() == null, "no customer");
        Assert.isTrue(cm.getDuplicates().size() == 0, "");
    }

    @Test
    void testLoadCompanyCustomerNoDuplicate() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByExternalId(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = service2Test.loadCustomerCompany("789", "123");

        Assert.notNull(cm, "");
        Assert.isTrue(cm.getDuplicates().size() == 0, "");
    }

    @Test
    void testLoadCompanyCustomerWithDuplicate() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        List<Customer> dup = new ArrayList<>();
        dup.add(cus);

        Mockito.when(customerDataLayer.findByCompanyNumber(ArgumentMatchers.anyString())).thenReturn(cus);
        Mockito.when(customerDataLayer.findByMasterExternalId(ArgumentMatchers.anyString())).thenReturn(dup);

        CustomerMatches cm = service2Test.loadCustomerCompany("789", "123");

        Assert.notNull(cm, "");
        Assert.isTrue(cm.hasDuplicates(), "");

        Assert.isTrue(cm.getDuplicates().iterator().next().getCompanyNumber() == cus.getCompanyNumber(), "");

    }
    @Test
    void testLoadCompanyCustomerByCompanyNumber() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByCompanyNumber(ArgumentMatchers.anyString())).thenReturn(cus);

        CustomerMatches cm = service2Test.loadCustomerCompany("789", "123");

        Assert.notNull(cm, "");
        Assert.isTrue(!cm.hasDuplicates(), "");
        Assert.isTrue(cm.getCustomer().getCompanyNumber() == cus.getCompanyNumber(), "");
        Assert.isTrue(cm.getMatchTerm() == MatchTerm.COMPANYNUMBER, "");
    }

    @Test
    void testLoadCompanyCustomerByCompanyNameExc() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByCompanyNumber(ArgumentMatchers.anyString())).thenReturn(cus);

        Assertions.assertThrows(ConflictException.class, () -> {
            CustomerMatches cm = service2Test.loadCustomerCompany("78", "123");
        });

    }

    @Test
    void testLoadCompanyCustomerByCompanyNameExcWrongType() {

        Customer cus = new Customer();
        cus.setCompanyNumber("123");
        cus.setCustomerType(CustomerType.PERSON);
        cus.setExternalId("789");

        Mockito.when(customerDataLayer.findByCompanyNumber(ArgumentMatchers.anyString())).thenReturn(cus);

        Assertions.assertThrows(ConflictException.class, () -> {
            CustomerMatches cm = service2Test.loadCustomerCompany("789", "123");
        });

    }

    @Test
    public void testLoadPersonCustomer() {
        Customer cus = new Customer();
        cus.setCustomerType(CustomerType.PERSON);
        cus.setName("ert");
        cus.setExternalId("jko");

        Mockito.when(customerDataLayer.findByExternalIdAndCustomerType(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(cus);

        CustomerMatches cm = service2Test.loadCustomer("ddd", CustomerType.PERSON, null);
        Assert.notNull(cm, "not null");
        Assert.notNull(cm.getCustomer(), "customer not null");

        Assert.isTrue(cm.getCustomer().getName() == cus.getName(), "customer name");

    }

    @Test
    public void testLoadPersonNotFounded() {
        Mockito.when(customerDataLayer.findByExternalIdAndCustomerType(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(null);

        CustomerMatches cm = service2Test.loadCustomer("ddd", CustomerType.PERSON, null);
        Assert.notNull(cm, "not null");
        Assert.isTrue(cm.getCustomer() == null, "customer not null");


    }

    @Test
    public void testLoadPersonCustomerCompany() {
        Customer cus = new Customer();
        cus.setCustomerType(CustomerType.COMPANY);
        cus.setName("ert");
        cus.setExternalId("jko");
        cus.setCompanyNumber("789");

        Mockito.when(customerDataLayer.findByExternalIdAndCustomerType(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(cus);

        CustomerMatches cm = service2Test.loadCustomer("ddd", CustomerType.COMPANY, "789");
        Assert.notNull(cm, "not null");
        Assert.notNull(cm.getCustomer(), "customer not null");

        Assert.isTrue(cm.getCustomer().getName() == cus.getName(), "customer name");

    }
}