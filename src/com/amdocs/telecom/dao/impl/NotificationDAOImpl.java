package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public boolean save(Notification n) {
        String sql = "INSERT INTO notifications (recipient_id, message, notification_type, created_date, read_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getRecipientId());
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getNotificationType());
            ps.setString(4, n.getCreatedDate());
            ps.setBoolean(5, n.isReadStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Notification> findByRecipientId(String recipientId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY notification_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recipientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getString("recipient_id"),
                            rs.getString("message"),
                            rs.getString("notification_type"),
                            rs.getString("created_date"),
                            rs.getBoolean("read_status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET read_status = 1 WHERE notification_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
