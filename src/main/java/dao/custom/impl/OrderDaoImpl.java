package dao.custom.impl;

import dao.util.CrudUtil;
import dao.util.HibernateUtil;
import db.DBConnection;
import dto.OrderDetailDto;
import dto.OrderDto;
import dao.custom.OrderDetailDao;
import dao.custom.OrderDao;
import entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
    @Override
    public boolean save(OrderDto dto) throws SQLException {
        /* Connection connection = null;
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
        return false;*/

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Orders order = new Orders(
                dto.getOrderId(),
                Date.valueOf(dto.getDate())
        );
        order.setCustomer(session.find(Customer.class,dto.getCustId()));
        session.save(order);

        List<OrderDetailDto> list = dto.getList(); //dto type

        for (OrderDetailDto detailDto:list) {
            OrderDetail orderDetail = new OrderDetail(
                    new OrderDetailsKey(detailDto.getOrderId(), detailDto.getItemCode()),
                    order,
                    session.find(Item.class, detailDto.getItemCode()),
                    detailDto.getQty(),
                    detailDto.getUnitPrice()
            );
            session.save(orderDetail);
        }

        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(OrderDto dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.find(Orders.class,id));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<OrderDto> getAll() throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Query query = session.createQuery("FROM Orders ");
        List<Orders> orderList = query.list();
        List<OrderDto> list = new ArrayList<>();
        for (Orders orders:orderList) {
            list.add(new OrderDto(
                    orders.getId(),
                    orders.getDate().toString(),
                    orders.getCustomer().getId(),
                    null
            ));
        }
        session.close();
        return list;
        /*
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
        return list;*/
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
