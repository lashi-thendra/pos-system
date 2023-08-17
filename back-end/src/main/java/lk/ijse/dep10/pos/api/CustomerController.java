package lk.ijse.dep10.pos.api;

import lk.ijse.dep10.pos.business.BOFactory;
import lk.ijse.dep10.pos.business.BOType;
import lk.ijse.dep10.pos.business.custom.CustomerBO;
import lk.ijse.dep10.pos.dto.CustomerDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin
public class CustomerController {

    @Autowired
    private BasicDataSource pool;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}")
    public void updateCustomer(@PathVariable("id") int customerId,
                               @RequestBody @Valid CustomerDTO customer) throws Exception{
        CustomerBO customerBO = BOFactory.getInstance().getBO(BOType.CUSTOMER, pool);
        customer.setId(customerId);
        customerBO.updateCustomer(customer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable("id") Integer customerId) throws Exception {
        CustomerBO customerBO = BOFactory.getInstance().getBO(BOType.CUSTOMER, pool);
        customerBO.deleteCustomerById(customerId);
    }


    @GetMapping
    public List<CustomerDTO> getCustomers(@RequestParam(value = "q", required = false) String query) throws Exception {
        CustomerBO customerBO = BOFactory.getInstance().getBO(BOType.CUSTOMER, pool);
        return customerBO.findCustomers(query);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerDTO saveCustomer(@RequestBody @Valid CustomerDTO customer) throws Exception {
        CustomerBO customerBO = BOFactory.getInstance().getBO(BOType.CUSTOMER, pool);
        return customerBO.saveCustomer(customer);
    }
}
