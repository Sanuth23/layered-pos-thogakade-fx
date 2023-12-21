package model;

import dto.ItemDto;
import java.sql.SQLException;
import java.util.List;

public interface ItemModel {
    boolean saveItem(ItemDto itemDto) throws SQLException, ClassNotFoundException;
    boolean updateItem(ItemDto itemDto) throws SQLException, ClassNotFoundException;
    boolean deleteItem(String code) throws SQLException, ClassNotFoundException;
    List<ItemDto> allItems() throws SQLException, ClassNotFoundException;
    ItemDto getItem(String code) throws SQLException, ClassNotFoundException;
}
