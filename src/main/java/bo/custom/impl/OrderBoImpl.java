package bo.custom.impl;

import bo.custom.OrderBo;
import dao.DaoFactory;
import dao.custom.OrderDao;
import dao.util.DaoType;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    private OrderDao orderDao = DaoFactory.getInstance().getDao(DaoType.ORDER);
    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException{
        return orderDao.save(dto);
    }

    @Override
    public List<OrderDto> allOrders() throws SQLException, ClassNotFoundException {
        return orderDao.getAll();
    }

    @Override
    public boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException {
        return orderDao.delete(orderId);
    }

    @Override
    public String  generateId() throws SQLException, ClassNotFoundException {
        String  id = orderDao.lastOrder().getOrderId();
        if (id!=null){
            int num = Integer.parseInt(id.split("[D]")[1]);
            num++;
            return String.format("D%03d",num);
        }else {
            return "D001";
        }
    }
}
