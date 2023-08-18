package lk.ijse.dep10.pos.api;

import lk.ijse.dep10.pos.business.custom.ItemBO;
import lk.ijse.dep10.pos.dto.ItemDTO;
import lk.ijse.dep10.pos.dto.util.ValidationGroups;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemBO itemBO;


    public ItemController(ItemBO itemBO) {
        this.itemBO = itemBO;
    }

    @GetMapping("/{code}")
    public ItemDTO getItem(@PathVariable String code) throws Exception {
        return itemBO.findItemByCode(code);
    }

    @GetMapping
    public List<ItemDTO> getItems(@RequestParam(value = "q", required = false) String query) throws Exception {
        if (query == null) query = "";
        return itemBO.findItems(query);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json")
    public void saveItem(@RequestBody @Validated({ValidationGroups.Save.class}) ItemDTO item) throws Exception {
        itemBO.saveItem(item);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{code}", consumes = "application/json")
    public void updateItem(@RequestBody ItemDTO item, @PathVariable String code) throws Exception {
        item.setCode(code);
        itemBO.updateItem(item);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{code}")
    public void deleteItem(@PathVariable String code) throws Exception {
        itemBO.deleteItemByCode(code);
    }
}
