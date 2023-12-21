package model.impl;

import db.DBConnection;
import dto.OrderDetailDto;
import dto.OrderDto;
import model.OrderDetailModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailModelImpl implements OrderDetailModel {

    @Override
    public boolean saveOrderDetails(List<OrderDetailDto> list) throws SQLException, ClassNotFoundException {
        boolean isDetailsSaved = true;
        for (OrderDetailDto dto:list) {
            String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
            PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
            pstm.setString(1,dto.getOrderId());
            pstm.setString(2,dto.getItemCode());
            pstm.setInt(3,dto.getQty());
            pstm.setDouble(4,dto.getUnitPrice());

            if(!(pstm.executeUpdate()>0)){
                isDetailsSaved = false;
            }
        }
        return isDetailsSaved;    }

    @Override
    public boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM OrderDetail WHERE orderId=? AND itemCode=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        pstm.setString(2,code);
        return pstm.executeUpdate()>0;
    }

    @Override
    public List<OrderDetailDto> allOrderDetails(String id) throws SQLException, ClassNotFoundException {
        List<OrderDetailDto> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetail WHERE orderId=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet result = pstm.executeQuery();
        while (result.next()){
            list.add(new OrderDetailDto(
                    result.getString(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getDouble(4)
            ));
        }
        return list;
    }
}
