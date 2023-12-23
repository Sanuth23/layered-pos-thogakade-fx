package dao.custom.impl;

import dao.util.CrudUtil;
import db.DBConnection;
import dto.OrderDetailDto;
import dao.custom.OrderDetailDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {

    @Override
    public boolean saveOrderDetails(List<OrderDetailDto> list) throws SQLException, ClassNotFoundException {
        boolean isDetailsSaved = true;
        for (OrderDetailDto dto:list) {
            String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";

            boolean b = (CrudUtil.execute(sql,dto.getOrderId(),dto.getItemCode(),dto.getQty(),dto.getUnitPrice()));
            if(!b){
                isDetailsSaved = false;
            }
        }
        return isDetailsSaved;
    }

    @Override
    public boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM OrderDetail WHERE orderId=? AND itemCode=?";

        return CrudUtil.execute(sql,id,code);
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
