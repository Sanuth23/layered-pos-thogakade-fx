package model.impl;

import db.DBConnection;
import dto.ItemDto;
import model.ItemModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemModelImpl implements ItemModel {
    @Override
    public boolean saveItem(ItemDto itemDto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Item VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,itemDto.getCode());
        pstm.setString(2,itemDto.getDescription());
        pstm.setDouble(3,itemDto.getPrice());
        pstm.setInt(4,itemDto.getQty());
        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateItem(ItemDto itemDto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,itemDto.getDescription());
        pstm.setDouble(2,itemDto.getPrice());
        pstm.setInt(3,itemDto.getQty());
        pstm.setString(4,itemDto.getCode());
        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Item WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,code);
        return pstm.executeUpdate()>0;
    }

    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {
        List<ItemDto> list = new ArrayList<>();
        String sql = "SELECT * FROM Item";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        while (result.next()){
            list.add(new ItemDto(result.getString(1),
                    result.getString(2),
                    result.getDouble(3),
                    result.getInt(4)
            ));
        }
        return list;
    }

    @Override
    public ItemDto getItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM item WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return new ItemDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3),
                    resultSet.getInt(4)
            );
        }
        return null;
    }
}
