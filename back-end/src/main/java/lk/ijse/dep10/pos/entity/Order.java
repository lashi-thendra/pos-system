package lk.ijse.dep10.pos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private int id;
    private Timestamp timestamp;

    public Order(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
