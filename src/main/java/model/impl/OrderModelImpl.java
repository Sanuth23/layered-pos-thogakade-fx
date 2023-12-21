package model.impl;

import db.DBConnection;
import dto.CustomerDto;
import dto.OrderDto;
import model.OrderDetailModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderModelImpl implements OrderModel {

    OrderDetailModel orderDetailModel = new OrderDetailModelImpl();
    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getInstanceOf().getConnection();
            connection.setAutoCommit(false);

            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, dto.getOrderId());
            pstm.setString(2, dto.getDate());
            pstm.setString(3, dto.getCustId());
            if (pstm.executeUpdate() > 0) {
                boolean isDetailSaved = orderDetailModel.saveOrderDetails(dto.getList());
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
    public OrderDto lastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

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

    @Override
    public boolean deleteOrder(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Orders WHERE id=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        return pstm.executeUpdate()>0;

    }

    @Override
    public List<OrderDto> allOrders() throws SQLException, ClassNotFoundException {
        List<OrderDto> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
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
}
