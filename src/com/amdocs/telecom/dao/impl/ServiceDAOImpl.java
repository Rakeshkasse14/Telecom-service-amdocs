package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.ServiceDAO;
import com.amdocs.telecom.enums.ServiceType;
import com.amdocs.telecom.model.TelecomService;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAOImpl implements ServiceDAO {

    @Override
    public TelecomService findById(int serviceId) {
        String sql = "SELECT * FROM telecom_services WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToService(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TelecomService> findByCustomerId(int customerId) {
        List<TelecomService> list = new ArrayList<>();
        String sql = "SELECT * FROM telecom_services WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToService(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TelecomService> findAll() {
        List<TelecomService> list = new ArrayList<>();
        String sql = "SELECT * FROM telecom_services";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRowToService(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private TelecomService mapRowToService(ResultSet rs) throws SQLException {
        return new TelecomService(
                rs.getInt("service_id"),
                rs.getString("service_code"),
                rs.getString("service_name"),
                ServiceType.valueOf(rs.getString("service_type")),
                rs.getInt("customer_id"),
                rs.getString("activation_date"),
                rs.getString("service_status")
        );
    }
}
