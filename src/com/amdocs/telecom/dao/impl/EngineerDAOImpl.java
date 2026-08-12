package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.EngineerDAO;
import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EngineerDAOImpl implements EngineerDAO {

    @Override
    public NetworkEngineer findById(int engineerId) {
        String sql = "SELECT * FROM network_engineers WHERE engineer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, engineerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToEngineer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NetworkEngineer findByEmployeeCode(String employeeCode) {
        String sql = "SELECT * FROM network_engineers WHERE employee_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToEngineer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NetworkEngineer> findAll() {
        List<NetworkEngineer> list = new ArrayList<>();
        String sql = "SELECT * FROM network_engineers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRowToEngineer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateWorkloadAndAvailability(int engineerId, int activeCount, boolean available) {
        String sql = "UPDATE network_engineers SET active_ticket_count = ?, availability = ? WHERE engineer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeCount);
            ps.setBoolean(2, available);
            ps.setInt(3, engineerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private NetworkEngineer mapRowToEngineer(ResultSet rs) throws SQLException {
        return new NetworkEngineer(
                rs.getInt("engineer_id"),
                rs.getString("employee_code"),
                rs.getString("engineer_name"),
                rs.getString("specialization"),
                rs.getString("region"),
                rs.getInt("experience_years"),
                rs.getBoolean("availability"),
                rs.getInt("active_ticket_count")
        );
    }
}
