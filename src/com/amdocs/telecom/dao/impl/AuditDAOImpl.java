package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.AuditDAO;
import com.amdocs.telecom.model.AuditLog;
import com.amdocs.telecom.util.DBConnection;
import com.amdocs.telecom.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditDAOImpl implements AuditDAO {

    @Override
    public boolean logAction(String action, String performedBy, String details) {
        String sql = "INSERT INTO audit_logs (action, performed_by, timestamp, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, performedBy);
            ps.setString(3, DateUtil.now());
            ps.setString(4, details);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<AuditLog> findAll() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY audit_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AuditLog(
                        rs.getInt("audit_id"),
                        rs.getString("action"),
                        rs.getString("performed_by"),
                        rs.getString("timestamp"),
                        rs.getString("details")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
