package model;

import dto.OrderDetailDto;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailModel {
    boolean saveOrderDetails(List<OrderDetailDto> list) throws SQLException, ClassNotFoundException;
    boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException;
    List<OrderDetailDto> allOrderDetails(String id) throws SQLException, ClassNotFoundException;

}
