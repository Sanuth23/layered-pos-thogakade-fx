package bo.custom.impl;

import bo.custom.OrderDetailBo;
import dao.DaoFactory;
import dao.custom.OrderDetailDao;
import dao.util.DaoType;
import dto.OrderDetailDto;

import java.sql.SQLException;
import java.util.List;

public class OrderDetailBoImpl implements OrderDetailBo {
    OrderDetailDao orderDetailDao = DaoFactory.getInstance().getDao(DaoType.ORDER_DETAIL);

    @Override
    public boolean deleteOrderDetail(String id, String code) throws SQLException, ClassNotFoundException {
        return orderDetailDao.deleteOrderDetail(id, code);
    }

    @Override
    public List<OrderDetailDto> orderItems(String id) throws SQLException, ClassNotFoundException {
        return orderDetailDao.allOrderDetails(id);
    }
}
