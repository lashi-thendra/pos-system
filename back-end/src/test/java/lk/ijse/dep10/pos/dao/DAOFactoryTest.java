package lk.ijse.dep10.pos.dao;

import lk.ijse.dep10.pos.dao.custom.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DAOFactoryTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("before each");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @AfterEach
    void tearDown() {
        System.out.println("After each");
    }

    @Test
    void getInstance() {

    }

    @Test
    void getDAO() {
        System.out.println(this);

        //exercises
//        CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOType.CUSTOMER);
//        ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);
//        OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
//        OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER_DETAIL);
//        OrderCustomerDAO orderCustomerDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER_CUSTOMER);
//        QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOType.QUERY);

        //verify
//        assertNotNull(customerDAO);
//        assertNotNull(itemDAO);
//        assertNotNull(orderDAO);
//        assertNotNull(orderDetailDAO);
//        assertNotNull(orderCustomerDAO);
//        assertNotNull(queryDAO);
    }
}