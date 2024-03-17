package com.example.demo;

import com.example.demo.app.services.ICustomerDataAccess;
import com.example.demo.repository.ICustomerDataLayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private ICustomerDataAccess srv;

    @Test
    void contextLoads() {
    }

    @Test
    void repoNotNull(){

        Assert.notNull(srv, "");


    }

}
