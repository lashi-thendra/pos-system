package lk.ijse.dep10.pos.dao.custom.impl;

import com.github.javafaker.Faker;
import lk.ijse.dep10.pos.dao.TestConfig;
import lk.ijse.dep10.pos.dao.custom.CustomerDAO;
import lk.ijse.dep10.pos.entity.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = TestConfig.class)
class CustomerDAOImplTest {

    private CustomerDAO customerDAO ;

    private Connection connection;
    @Autowired
    private EmbeddedDatabase embeddedDatabase;


    @BeforeEach
    void setUp() throws SQLException {
        connection = embeddedDatabase.getConnection();
//        customerDAO.setConnection(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @RepeatedTest(5)
    void save() throws Exception {
        Faker faker = new Faker();
        var customer = new Customer(faker.name().fullName(), faker.address().cityName(),
                faker.regexify("\\d{3}-\\d{7}"));
        var previousCount = customerDAO.count();
        Customer savedCustomer = customerDAO.save(customer);
        var newCount = customerDAO.count();

        System.out.println(savedCustomer);
        assertNotEquals(0, savedCustomer.getId());
        assertEquals(previousCount + 1, newCount);
    }

    @Test
    void update() throws Exception {
        Customer johnCustomer = new Customer("John", "Matara", "077-1234567");
        Customer savedJohn = customerDAO.save(johnCustomer);
        savedJohn.setName("John Harry");
        savedJohn.setAddress("Mirissa");
        savedJohn.setContact("055-12378541");

        assertDoesNotThrow(() -> customerDAO.update(savedJohn));
        Customer updatedJohn = customerDAO.findById(savedJohn.getId()).get();
        assertEquals(savedJohn, updatedJohn);
    }
}