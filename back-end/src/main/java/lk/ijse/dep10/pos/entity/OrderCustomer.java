package lk.ijse.dep10.pos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomer implements Serializable {
    private int orderId;
    private int customerId;
}
