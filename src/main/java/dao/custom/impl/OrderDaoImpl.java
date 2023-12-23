package dao.custom.impl;

import dao.util.CrudUtil;
import db.DBConnection;
import dto.OrderDto;
import dao.custom.OrderDetailDao;
import dao.custom.OrderDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
    @Override
    public boolean save(OrderDto dto) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getInstanceOf().getConnection();
            connection.setAutoCommit(false);

            String sql = "INSERT INTO orders VALUES(?,?,?)";

            if (CrudUtil.execute(sql,dto.getOrderId(),dto.getDate(),dto.getCustId())) {
                boolean isDetailSaved = orderDetailDao.saveOrderDetails(dto.getList());
                if (isDetailSaved) {
                    connection.commit();
                    return true;
                }
            }
        }catch (SQLException | ClassNotFoundException ex){
            connection.rollback();
            ex.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }
        return false;
    }

    @Override
    public boolean update(OrderDto dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Orders WHERE id=?";

        return CrudUtil.execute(sql,id);
    }

    @Override
    public List<OrderDto> getAll() throws SQLException, ClassNotFoundException {
        List<OrderDto> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders";
        ResultSet result = CrudUtil.execute(sql);
        while (result.next()){
            list.add(new OrderDto(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    null
            ));
        }
        return list;
    }

    @Override
    public OrderDto lastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        ResultSet resultSet = CrudUtil.execute(sql);

        if (resultSet.next()){
            return new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null
            );
        }

        return null;
    }
}
