package lk.ijse.dep10.pos.business.util;

import lk.ijse.dep10.pos.dto.CustomerDTO;
import lk.ijse.dep10.pos.entity.Customer;
import org.modelmapper.ModelMapper;

public class Transformer {
    private final ModelMapper mapper = new ModelMapper();

    public Customer toCustomerEntity(CustomerDTO customerDTO){
        return mapper.map(customerDTO, Customer.class);
    }

    public CustomerDTO fromCustomerEntity(Customer customerEntity){
        return mapper.map(customerEntity, CustomerDTO.class);
    }
}
