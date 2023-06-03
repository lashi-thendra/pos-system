package lk.ijse.dep10.pos.api;

import lk.ijse.dep10.pos.dto.CustomerDTO;
import lk.ijse.dep10.pos.dto.ItemDTO;
import lk.ijse.dep10.pos.dto.ResponseErrorDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private BasicDataSource pool;

    @GetMapping
    public ResponseEntity<?> getItems(@RequestParam(value = "q", required = false) String query){
        System.out.println("get Items method");
        if(query == null) query = "";
        try(Connection connection = pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM item WHERE code LIKE ? OR description LIKE ?");
            query = "%" +  query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            ResultSet rst = stm.executeQuery();
            List<ItemDTO> itemList = new ArrayList<>();
            while (rst.next()){
                int id = rst.getInt("code");
                String description = rst.getString("description");
                int qty = rst.getInt("qty");
                BigDecimal unitPrice = rst.getBigDecimal("unit_price");
                itemList.add(new ItemDTO(id, description, qty, unitPrice));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-count", itemList.size() + "");
            return new ResponseEntity<>(itemList, headers, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseErrorDTO(500, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody ItemDTO item){
        try(Connection connection =  pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("INSERT INTO item (description, qty, unit_price) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, item.getDescription());
            stm.setInt(2, item.getQty());
            stm.setBigDecimal(3, item.getPrice());
            stm.executeUpdate();

            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int code = generatedKeys.getInt(1);
            item.setCode(code);
            return new ResponseEntity<>(item, HttpStatus.CREATED);
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

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteItem(@PathVariable("code") String code){
        try(Connection connection  = pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("DELETE FROM item WHERE code = ?");
            stm.setString(1, code);
            int effectedRows = stm.executeUpdate();
            if(effectedRows == 1){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                ResponseErrorDTO response = new ResponseErrorDTO(404, "Item code Not Found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().equals("23000")) {
                return new ResponseEntity<>(
                        new ResponseErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()),
                        HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(
                        new ResponseErrorDTO(500, e.getMessage()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }



}
