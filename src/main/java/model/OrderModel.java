package model;

import dto.CustomerDto;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderModel {
    boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException;
    OrderDto lastOrder() throws SQLException, ClassNotFoundException;
    boolean deleteOrder(String id) throws SQLException, ClassNotFoundException;
    List<OrderDto> allOrders() throws SQLException, ClassNotFoundException;
}
