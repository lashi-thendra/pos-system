package lk.ijse.dep10.pos.business.custom.impl;

import lk.ijse.dep10.pos.business.custom.CustomerBO;
import lk.ijse.dep10.pos.business.exception.BusinessException;
import lk.ijse.dep10.pos.business.exception.BusinessExceptionType;
import lk.ijse.dep10.pos.business.util.Transformer;
import lk.ijse.dep10.pos.dao.custom.CustomerDAO;
import lk.ijse.dep10.pos.dao.custom.OrderCustomerDAO;
import lk.ijse.dep10.pos.dto.CustomerDTO;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerBOImpl implements CustomerBO {

    private final Transformer transformer;

    private final DataSource dataSource;
    private final CustomerDAO customerDAO;
    private final OrderCustomerDAO orderCustomerDAO;

    public CustomerBOImpl(Transformer transformer, DataSource dataSource, CustomerDAO customerDAO, OrderCustomerDAO orderCustomerDAO) {
        this.transformer = transformer;
        this.dataSource = dataSource;
        this.customerDAO = customerDAO;
        this.orderCustomerDAO = orderCustomerDAO;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            customerDAO.setConnection(connection);
            if (existContact(customerDTO.getContact())) {
                throw new BusinessException(BusinessExceptionType.DUPLICATE_RECORD,
                        "Save failed: Contact number: " + customerDTO.getContact() + " already exists");
            }

            return transformer.fromCustomerEntity(customerDAO.save(transformer.toCustomerEntity(customerDTO)));
        }
    }

    private boolean existContact(String contact){
        return false;
    }

    @Override
    public void updateCustomer(CustomerDTO customerDTO) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            customerDAO.setConnection(connection);

            if (existContact(customerDTO.getContact())){
                throw new BusinessException(BusinessExceptionType.DUPLICATE_RECORD,
                        "Update failed: Contact number: " + customerDTO.getContact() + " already exists");
            }
            customerDAO.update(transformer.toCustomerEntity(customerDTO));
        }
    }

    @Override
    public void deleteCustomerById(int customerId) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            customerDAO.setConnection(connection);
            orderCustomerDAO.setConnection(connection);

            if (orderCustomerDAO.existsOrderByCustomerId(customerId)) {
                throw new BusinessException(BusinessExceptionType.INTEGRITY_VIOLATION, "Delete failed: Customer ID: " + customerId + " is already associated with some orders");
            }

            customerDAO.deleteById(customerId);
        }
    }

    @Override
    public CustomerDTO findCustomerByIdOrContact(String idOrContact) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            customerDAO.setConnection(connection);

            return customerDAO.findCustomerByIdOrContact(idOrContact)
                    .map(transformer::fromCustomerEntity)
                    .orElseThrow(() -> new BusinessException(BusinessExceptionType.RECORD_NOT_FOUND,
                            "No customer record is associated with the id or contact: " + idOrContact));
        }
    }

    @Override
    public List<CustomerDTO> findCustomers(String query) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            customerDAO.setConnection(connection);

            return customerDAO.findCustomers(query).stream()
                    .map(transformer::fromCustomerEntity).collect(Collectors.toList());
        }
    }
}
