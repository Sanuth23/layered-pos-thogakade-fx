package dao.custom.impl;

import dao.util.CrudUtil;
import db.DBConnection;
import dto.CustomerDto;
import dao.custom.CustomerDao;
import entity.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public Customer searchCustomer(String id) {
        return null;
    }

    @Override
    public Customer getCustomer(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Customer WHERE id=?";
        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            );
        }
        return null;
    }

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Customer VALUES(?,?,?,?)";
//        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
//        pstm.setString(1,entity.getId());
//        pstm.setString(2,entity.getName());
//        pstm.setString(3,entity.getAddress());
//        pstm.setDouble(4,entity.getSalary());
//        return pstm.executeUpdate()>0;

        return CrudUtil.execute(sql,entity.getId(),entity.getName(),entity.getAddress(),entity.getSalary());
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Customer SET name=?, address=?, salary=? WHERE id=?";
//        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
//        pstm.setString(1,entity.getName());
//        pstm.setString(2,entity.getAddress());
//        pstm.setDouble(3,entity.getSalary());
//        pstm.setString(4,entity.getId());
//        return pstm.executeUpdate()>0;

        return CrudUtil.execute(sql,entity.getName(),entity.getAddress(),entity.getSalary(),entity.getId());
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Customer WHERE id=?";
//        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
//        pstm.setString(1,value);
//        return pstm.executeUpdate()>0;

        return CrudUtil.execute(sql,value);
    }

    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer";
//        PreparedStatement pstm = DBConnection.getInstanceOf().getConnection().prepareStatement(sql);
//        ResultSet result = pstm.executeQuery();

        ResultSet result = CrudUtil.execute(sql);
        while (result.next()){
            list.add(new Customer(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getDouble(4)
            ));
        }
        return list;
    }
}
