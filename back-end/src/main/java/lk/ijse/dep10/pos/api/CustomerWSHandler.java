package lk.ijse.dep10.pos.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.ijse.dep10.pos.dto.CustomerDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class CustomerWSHandler extends TextWebSocketHandler {

    @Autowired
    private BasicDataSource pool;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(message.getPayload());

        try(Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM customer WHERE id=?");
            stm.setString(1, message.getPayload());
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");

                CustomerDTO customer = new CustomerDTO(id, name, address, contact);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(customer)));
            }
        }
    }
}
