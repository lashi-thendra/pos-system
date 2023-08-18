package lk.ijse.dep10.pos.dao.custom.impl;

import lk.ijse.dep10.pos.dao.custom.QueryDAO;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class QueryDAOImpl implements QueryDAO {

    private Connection connection;

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
