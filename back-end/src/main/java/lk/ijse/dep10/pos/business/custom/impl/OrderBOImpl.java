package lk.ijse.dep10.pos.business.custom.impl;

import lk.ijse.dep10.pos.business.custom.OrderBO;
import lk.ijse.dep10.pos.business.exception.BusinessException;
import lk.ijse.dep10.pos.business.exception.BusinessExceptionType;
import lk.ijse.dep10.pos.business.util.Transformer;
import lk.ijse.dep10.pos.dao.custom.*;
import lk.ijse.dep10.pos.dto.ItemDTO;
import lk.ijse.dep10.pos.dto.OrderDTO;
import lk.ijse.dep10.pos.dto.OrderDTO2;
import lk.ijse.dep10.pos.entity.Item;
import lk.ijse.dep10.pos.entity.Order;
import lk.ijse.dep10.pos.entity.OrderCustomer;
import lk.ijse.dep10.pos.entity.OrderDetail;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class OrderBOImpl implements OrderBO {

    private final OrderDAO orderDAO;
    private final OrderDetailDAO orderDetailDAO;
    private final ItemDAO itemDAO;
    private final CustomerDAO customerDAO;
    private final OrderCustomerDAO orderCustomerDAO;
    private final QueryDAO queryDAO;
    private final Transformer transformer;

    public OrderBOImpl(OrderDAO orderDAO, OrderDetailDAO orderDetailDAO,
                       ItemDAO itemDAO, CustomerDAO customerDAO, OrderCustomerDAO orderCustomerDAO, QueryDAO queryDAO, Transformer transformer) {
        this.orderDAO = orderDAO;
        this.orderDetailDAO = orderDetailDAO;
        this.itemDAO = itemDAO;
        this.customerDAO = customerDAO;
        this.orderCustomerDAO = orderCustomerDAO;
        this.queryDAO = queryDAO;
        this.transformer = transformer;
    }

    @Override
    public Integer placeOrder(OrderDTO orderDTO) throws Exception {

        //saving the order
        Order order = orderDAO.save(new Order(Timestamp.valueOf(orderDTO.getDateTime())));

        // if order has an associated customer
        if (orderDTO.getCustomer() != null) {

            // if customer exists in DB
            if (!customerDAO.findById(orderDTO.getCustomer().getId())
                    .map(transformer::fromCustomerEntity)
                    .orElseThrow(() -> new BusinessException(BusinessExceptionType.RECORD_NOT_FOUND,
                            "Order failed: Customer ID: " + orderDTO.getCustomer().getId() + " does not exist"))
                    .equals(orderDTO.getCustomer()))
                throw new BusinessException(BusinessExceptionType.INTEGRITY_VIOLATION,
                        "Order failed: Provided customer data does not match");

            //save customer with this order
            orderCustomerDAO.save(new OrderCustomer(order.getId(), orderDTO.getCustomer().getId()));
        }

        // saving order details
        for (ItemDTO itemDTO : orderDTO.getItemList()) {

            // item exists in the db?
            Item item = itemDAO.findById(itemDTO.getCode()).orElseThrow(() ->
                    new BusinessException(BusinessExceptionType.RECORD_NOT_FOUND,
                            "Order failed: Item code: " + itemDTO.getCode() + " does not exist"));

            // if exists checking for integrity
            if (!(item.getDescription().equals(itemDTO.getDescription()) &&
                    item.getUnitPrice().setScale(2).equals(itemDTO.getUnitPrice().setScale(2))))
                throw new BusinessException(BusinessExceptionType.INTEGRITY_VIOLATION,
                        "Order failed: Provided item data for Item code:" + itemDTO.getCode() + " does not match");

            // request quantity can be satisfied?
            if (item.getQty() < itemDTO.getQty())
                throw new BusinessException(BusinessExceptionType.INTEGRITY_VIOLATION,
                        "Order failed: Insufficient stock for the Item code: " + itemDTO.getQty());

            // if everything is okay. saving the order details
            OrderDetail orderDetailEntity = transformer.toOrderDetailEntity(itemDTO);
            orderDetailEntity.getOrderDetailPK().setOrderId(order.getId());
            orderDetailDAO.save(orderDetailEntity);

            // updating the stocks
            item.setQty(item.getQty() - itemDTO.getQty());
            itemDAO.update(item);
        }

        return order.getId();

    }

    @Override
    public List<OrderDTO2> searchOrders(String query) throws Exception {
        return queryDAO.findOrdersByQuery(query);
    }
}
