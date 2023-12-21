package model;

import dto.CustomerDto;
import dto.ItemDto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerModel {
    boolean saveCustomer(CustomerDto customerDto) throws SQLException, ClassNotFoundException;
    boolean updateCustomer(CustomerDto customerDto) throws SQLException, ClassNotFoundException;
    boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException;
    List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException;
    CustomerDto searchCustomer(String id);
    CustomerDto getCustomer(String id) throws SQLException, ClassNotFoundException;
}
