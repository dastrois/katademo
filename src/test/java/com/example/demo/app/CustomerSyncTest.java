package com.example.demo.app;

import com.example.demo.app.impl.CustomerSync;
import com.example.demo.app.services.impl.CustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.exception.ConflictException;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalAddress;
import com.example.demo.model.vo.common.ExternalCustomer;
import com.example.demo.model.vo.common.ExternalShoppingItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class CustomerSyncTest {

    @InjectMocks
    private CustomerSync srv2Test;

    @Mock
    private CustomerDataAccess customerDataAccess;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testExc(){

        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCustomerCompany();
        externalCustomer.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);
        customer.setExternalId(externalId);
        customer.setCustomerType(CustomerType.PERSON);


        CustomerMatches cm = new CustomerMatches();
        cm.setCustomer(customer);

        when(customerDataAccess.loadCompanyCustomer(anyString(), anyString())).thenReturn(cm);
        when(customerDataAccess.createCustomerRecord(any())).thenReturn(customer);

        Assertions.assertThrows(ConflictException.class, () -> {
           boolean created = srv2Test.syncWithDataLayer(externalCustomer);
        });

    }

    @Test
    public void syncCompanyByExternalId(){


        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCustomerCompany();
        externalCustomer.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);
        customer.setExternalId(externalId);

//        ICustomerDataLayer db = mock(ICustomerDataLayer.class);
//        when(db.findByExternalId(externalId)).thenReturn(customer);
//
//        CustomerDataAccess cda = mock(CustomerDataAccess.class);
//        when(cda.loadCompanyCustomer(any(), any())).thenReturn(new CustomerMatches());

        when(customerDataAccess.loadCompanyCustomer(anyString(), anyString())).thenReturn(new CustomerMatches());
        when(customerDataAccess.createCustomerRecord(any())).thenReturn(customer);

        // ACT
        boolean created = srv2Test.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertTrue(created);
//        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
//        verify(customerDataAccess, atLeastOnce()).save(argument.capture());  // updateCustomerRecord(argument.capture());
//        Customer updatedCustomer = argument.getValue();
//        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
//        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
//        assertNull(updatedCustomer.getMasterExternalId());
//        assertEquals(externalCustomer.getCompanyNumber(), updatedCustomer.getCompanyNumber());
//        assertEquals(externalCustomer.getPostalAddress(), updatedCustomer.getAddress());
//        assertEquals(externalCustomer.getShoppingLists(), updatedCustomer.getShoppingLists());
//        assertEquals(CustomerType.COMPANY, updatedCustomer.getCustomerType());
//        assertNull(updatedCustomer.getPreferredStore());
    }


    private ExternalCustomer createExternalCustomerCompany() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setName("Acme Inc.");
        externalCustomer.setAddress(makeAddress());
        externalCustomer.setCompanyNumber("470813-8895");
        externalCustomer.setShoppingLists(makeShoppingList());
        return externalCustomer;
    }

    private Customer createCustomerWithSameCompanyAs(ExternalCustomer externalCustomer) {
        Customer customer = new Customer();
        customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");
        return customer;
    }

    private ExternalAddress makeAddress(){
        ExternalAddress address = new ExternalAddress();
        address.setCity("Helsingborg");
        address.setStreet("123 main st");
        address.setPostalCode("SE-123 45");
        return address;
    }

    private List<ExternalShoppingItem> makeShoppingList(){

        List<ExternalShoppingItem> shoppingList = new ArrayList<>();

        shoppingList.add(makeShoppingItem("lipstick"));
        shoppingList.add(makeShoppingItem("blusher"));

        return shoppingList;
    }

    private ExternalShoppingItem makeShoppingItem(String product){
        ExternalShoppingItem ret = new ExternalShoppingItem();
        ret.setProducts(product);
        return ret;
    }
}
