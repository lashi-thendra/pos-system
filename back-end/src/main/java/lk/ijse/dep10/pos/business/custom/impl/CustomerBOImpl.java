package lk.ijse.dep10.pos.business.custom.impl;

import lk.ijse.dep10.pos.business.custom.CustomerBO;
import lk.ijse.dep10.pos.business.exception.BusinessException;
import lk.ijse.dep10.pos.business.exception.BusinessExceptionType;
import lk.ijse.dep10.pos.business.util.Transformer;
import lk.ijse.dep10.pos.dao.DAOFactory;
import lk.ijse.dep10.pos.dao.DAOType;
import lk.ijse.dep10.pos.dao.custom.CustomerDAO;
import lk.ijse.dep10.pos.dto.CustomerDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class CustomerBOImpl implements CustomerBO {

    private final Transformer transformer = new Transformer();

    private final DataSource dataSource;

    public CustomerBOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOType.CUSTOMER);

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
    public void deleteCustomerByCode(int customerId) throws Exception {

    }

    @Override
    public CustomerDTO findCustomerByIdOrContact(String idOrContact) throws Exception {
        return null;
    }

    @Override
    public List<CustomerDTO> findCustomers(String query) throws Exception {
        return null;
    }
}
