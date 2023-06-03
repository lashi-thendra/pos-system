package lk.ijse.dep10.pos.api;

import lk.ijse.dep10.pos.dto.CustomerDTO;
import lk.ijse.dep10.pos.dto.ResponseErrorDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private BasicDataSource pool;

    @GetMapping
    public ResponseEntity<?> getCustomers(@RequestParam(value = "q", required = false) String query){
        if(query == null) query = "";
        try(Connection connection = pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM customer WHERE id LIKE ? OR NAME LIKE ?");
            query = "%" +  query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            ResultSet rst = stm.executeQuery();
            List<CustomerDTO> customerList = new ArrayList<>();
            while (rst.next()){
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                customerList.add(new CustomerDTO(id, name, address, contact));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-count", customerList.size() + "");
            return new ResponseEntity<>(customerList, headers, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseErrorDTO(500, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerDTO customer){
        try(Connection connection =  pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("INSERT INTO customer (name, address, contact) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, customer.getName());
            stm.setString(2, customer.getAddress());
            stm.setString(3, customer.getContact());
            stm.executeUpdate();

            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            customer.setId(id);
            return new ResponseEntity<>(customer, HttpStatus.CREATED);
        } catch (SQLException e) {
            if(e.getSQLState().equals("2300")){
                return new ResponseEntity<>(new ResponseErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()),
                        HttpStatus.CONFLICT);
            }else{
                return new ResponseEntity<>(new ResponseErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

}
