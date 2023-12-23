package dao.custom;

import dao.SuperDao;
import dto.OrderDetailDto;
import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDao extends SuperDao {
    boolean saveOrderDetails(List<OrderDetailDto> list) throws SQLException, ClassNotFoundException;
    boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException;
    List<OrderDetailDto> allOrderDetails(String id) throws SQLException, ClassNotFoundException;

}
