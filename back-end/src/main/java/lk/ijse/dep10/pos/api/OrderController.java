package lk.ijse.dep10.pos.api;

import lk.ijse.dep10.pos.dto.ItemDTO;
import lk.ijse.dep10.pos.dto.OrderDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.*;


@Controller
@CrossOrigin
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private BasicDataSource pool;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public int saveOrder(@RequestBody OrderDTO order) throws Exception{
        System.out.println("working");
        Connection connection = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);

            /* Let's save the order first */
            PreparedStatement stmOrder = connection
                    .prepareStatement("INSERT INTO `order` (`datetime`) VALUES (?)",
                            Statement.RETURN_GENERATED_KEYS);
            stmOrder.setTimestamp(1, Timestamp.valueOf(order.getDateTime()));
            stmOrder.executeUpdate();
            ResultSet generatedKeys = stmOrder.getGeneratedKeys();
            generatedKeys.next();
            int orderId = generatedKeys.getInt(1);

            if (order.getCustomer() != null){
                PreparedStatement stmOrderCustomer = connection
                        .prepareStatement("INSERT INTO order_customer (order_id, customer_id) VALUES (?, ?)");
                stmOrderCustomer.setInt(1, orderId);
                stmOrderCustomer.setInt(2, order.getCustomer().getId());
                stmOrderCustomer.executeUpdate();
            }

            PreparedStatement stmOrderItem = connection.
                    prepareStatement("INSERT INTO order_detail (order_id, item_code, unit_price, qty) VALUES (?, ?, ?, ?)");
            PreparedStatement stmUpdateStock = connection
                    .prepareStatement("UPDATE item SET qty = qty - ? WHERE code = ?");
            for (ItemDTO orderItem : order.getItemList()) {
                stmOrderItem.setInt(1, orderId);
                stmOrderItem.setInt(2, orderItem.getCode());
                stmOrderItem.setBigDecimal(3, orderItem.getPrice());
                stmOrderItem.setInt(4, orderItem.getQty());
                stmOrderItem.executeUpdate();

                stmUpdateStock.setInt(1, orderItem.getQty());
                stmUpdateStock.setInt(2, orderItem.getCode());
                stmUpdateStock.executeUpdate();

            }
            connection.commit();
            return orderId;

        } catch (Throwable t) {
            connection.rollback();
            t.printStackTrace();
            throw new RuntimeException(t);
        } finally {
            connection.setAutoCommit(true);
        }
    }


}