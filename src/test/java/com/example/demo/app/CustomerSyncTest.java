package com.example.demo.app;

import com.example.demo.app.impl.CustomerSync;
import com.example.demo.app.services.impl.CustomerDataAccess;
import com.example.demo.model.constant.CustomerType;
import com.example.demo.model.dao.Customer;
import com.example.demo.model.vo.CustomerMatches;
import com.example.demo.model.vo.common.ExternalAddress;
import com.example.demo.model.vo.common.ExternalCustomer;
import com.example.demo.model.vo.common.ExternalShoppingItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

        CustomerMatches cm = new CustomerMatches();
        cm.setCustomer(customer);

        when(customerDataAccess.loadCustomerCompany(anyString(), anyString())).thenReturn(cm);
        when(customerDataAccess.upSaveCustomer(any())).thenReturn(customer);

        boolean created = srv2Test.syncWithDataLayer(externalCustomer);
        Assert.isTrue(created == false, "");
    }

    @Test
    public void syncCompanyByExternalId(){


        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCustomerCompany();
        externalCustomer.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);
        customer.setExternalId(externalId);

        when(customerDataAccess.loadCustomerCompany(anyString(), anyString())).thenReturn(new CustomerMatches());
        when(customerDataAccess.upSaveCustomer(any())).thenReturn(customer);

        // ACT
        boolean created = srv2Test.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertTrue(created);
        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
        verify(customerDataAccess, atLeastOnce()).upSaveCustomer(argument.capture());  // updateCustomerRecord(argument.capture());
        Customer updatedCustomer = argument.getValue();
        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
        assertEquals(externalCustomer.getExternalId(),updatedCustomer.getMasterExternalId());
        assertEquals(externalCustomer.getCompanyNumber(), updatedCustomer.getCompanyNumber());
        assertEquals(externalCustomer.getPostalAddress().getStreet(), updatedCustomer.getAddress().getStreet());
        assertEquals(externalCustomer.getShoppingLists().size(), updatedCustomer.getShoppingLists().size());
        assertEquals(CustomerType.COMPANY, updatedCustomer.getCustomerType());
        assertEquals(0, updatedCustomer.getBonusPointBalance());
        assertNull(updatedCustomer.getPreferredStore());
    }
    @Test
    public void syncPersonByExternalId(){


        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCustomerPerson();
        externalCustomer.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);
        customer.setExternalId(externalId);

        when(customerDataAccess.loadCustomer(anyString(), ArgumentMatchers.any(), any())).thenReturn(new CustomerMatches());
        when(customerDataAccess.upSaveCustomer(any())).thenReturn(customer);

        // ACT
        boolean created = srv2Test.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertTrue(created);
        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
        verify(customerDataAccess, atLeastOnce()).upSaveCustomer(argument.capture());  // updateCustomerRecord(argument.capture());
        Customer updatedCustomer = argument.getValue();
        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
        assertEquals(externalCustomer.getExternalId(),updatedCustomer.getMasterExternalId());
        assertEquals(externalCustomer.getCompanyNumber(), updatedCustomer.getCompanyNumber());
        assertEquals(externalCustomer.getPostalAddress().getStreet(), updatedCustomer.getAddress().getStreet());
        assertEquals(externalCustomer.getShoppingLists().size(), updatedCustomer.getShoppingLists().size());
        assertEquals(CustomerType.PERSON, updatedCustomer.getCustomerType());
        assertEquals(100, updatedCustomer.getBonusPointBalance());
        assertNull(updatedCustomer.getPreferredStore());
    }


    private ExternalCustomer createExternalCustomerCompany() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setName("Acme Inc.");
        externalCustomer.setAddress(makeAddress());
        externalCustomer.setCompanyNumber("470813-8895");
        externalCustomer.setShoppingLists(makeShoppingList());
        externalCustomer.setBonusPointBalance(100);
        return externalCustomer;
    }
    private ExternalCustomer createExternalCustomerPerson() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setName("toto");
        externalCustomer.setAddress(makeAddress());
        externalCustomer.setShoppingLists(makeShoppingList());
        externalCustomer.setBonusPointBalance(100);
        return externalCustomer;
    }

    private Customer createCustomerWithSameCompanyAs(ExternalCustomer externalCustomer) {
        Customer customer = new Customer();
        customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");
        return customer;
    }
    private Customer createSameCustomerNoCompanyAs(ExternalCustomer externalCustomer) {
        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.PERSON);
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
