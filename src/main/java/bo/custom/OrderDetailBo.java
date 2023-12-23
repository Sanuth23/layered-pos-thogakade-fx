package bo.custom;

import bo.SuperBo;
import dto.OrderDetailDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailBo extends SuperBo {
    boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException;
    List<OrderDetailDto> orderItems(String id) throws SQLException, ClassNotFoundException;
}
