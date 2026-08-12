package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.CustomerDAO;
import com.amdocs.telecom.enums.CustomerType;
import com.amdocs.telecom.model.Customer;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public Customer findById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer findByCustomerNumber(String customerNumber) {
        String sql = "SELECT * FROM customers WHERE customer_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean save(Customer customer) {
        String sql = "INSERT INTO customers (customer_number, customer_name, email, mobile_number, customer_type, city, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getCustomerNumber());
            ps.setString(2, customer.getCustomerName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getMobileNumber());
            ps.setString(5, customer.getCustomerType().name());
            ps.setString(6, customer.getCity());
            ps.setString(7, customer.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) customer.setCustomerId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET customer_name=?, email=?, mobile_number=?, customer_type=?, city=?, status=? WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getMobileNumber());
            ps.setString(4, customer.getCustomerType().name());
            ps.setString(5, customer.getCity());
            ps.setString(6, customer.getStatus());
            ps.setInt(7, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("customer_number"),
                rs.getString("customer_name"),
                rs.getString("email"),
                rs.getString("mobile_number"),
                CustomerType.valueOf(rs.getString("customer_type")),
                rs.getString("city"),
                rs.getString("status")
        );
    }
}
