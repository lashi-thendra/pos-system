package lk.ijse.dep10.pos.business.custom.impl;

import lk.ijse.dep10.pos.business.custom.OrderBO;
import lk.ijse.dep10.pos.dto.OrderDTO;
import lk.ijse.dep10.pos.dto.OrderDTO2;

import javax.sql.DataSource;
import java.util.List;

public class OrderBOImpl implements OrderBO {

    private final DataSource dataSource;

    public OrderBOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Integer placeOrder(OrderDTO orderDTO) throws Exception {
        return null;
    }

    @Override
    public List<OrderDTO2> searchOrders(String query) throws Exception {
        return null;
    }
}
