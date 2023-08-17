package lk.ijse.dep10.pos.business.custom.impl;

import lk.ijse.dep10.pos.business.custom.ItemBO;
import lk.ijse.dep10.pos.dto.ItemDTO;

import javax.sql.DataSource;
import java.util.List;

public class ItemBOImpl implements ItemBO {

    private final DataSource dataSource;

    public ItemBOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveItem(ItemDTO itemDTO) throws Exception {

    }

    @Override
    public void updateItem(ItemDTO itemDTO) throws Exception {

    }

    @Override
    public void deleteItemByCode(String itemCode) throws Exception {

    }

    @Override
    public ItemDTO findItemByCode(String itemCode) throws Exception {
        return null;
    }

    @Override
    public List<ItemDTO> findItems(String query) throws Exception {
        return null;
    }
}
