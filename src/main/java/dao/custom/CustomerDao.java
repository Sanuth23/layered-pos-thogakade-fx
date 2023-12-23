package dao.custom;

import dao.CrudDao;
import dto.CustomerDto;
import entity.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDao extends CrudDao<Customer> {
//    boolean saveCustomer(CustomerDto customerDto) throws SQLException, ClassNotFoundException;
//    boolean updateCustomer(CustomerDto customerDto) throws SQLException, ClassNotFoundException;
//    boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException;
//    List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException;
        Customer searchCustomer(String id);
        Customer getCustomer(String id) throws SQLException, ClassNotFoundException;
}
