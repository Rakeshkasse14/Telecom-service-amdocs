package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.TicketDAO;
import com.amdocs.telecom.enums.*;
import com.amdocs.telecom.model.TicketStatusHistory;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.util.DBConnection;
import com.amdocs.telecom.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {

    @Override
    public TroubleTicket findById(int ticketId) {
        String sql = "SELECT * FROM trouble_tickets WHERE ticket_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToTicket(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TroubleTicket findByTicketNumber(String ticketNumber) {
        String sql = "SELECT * FROM trouble_tickets WHERE ticket_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToTicket(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TroubleTicket> findByCustomerId(int customerId) {
        List<TroubleTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM trouble_tickets WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TroubleTicket> findByEngineerId(int engineerId) {
        List<TroubleTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM trouble_tickets WHERE assigned_engineer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, engineerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TroubleTicket> findAll() {
        List<TroubleTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM trouble_tickets";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRowToTicket(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TroubleTicket> findByStatus(TicketStatus status) {
        List<TroubleTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM trouble_tickets WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean save(TroubleTicket ticket) {
        String sql = "INSERT INTO trouble_tickets (ticket_number, customer_id, service_id, category, description, " +
                "priority, severity, created_date, assigned_engineer_id, status, sla_deadline, sla_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getTicketNumber());
            ps.setInt(2, ticket.getCustomerId());
            ps.setInt(3, ticket.getServiceId());
            ps.setString(4, ticket.getCategory().name());
            ps.setString(5, ticket.getDescription());
            ps.setString(6, ticket.getPriority().name());
            ps.setString(7, ticket.getSeverity());
            ps.setString(8, ticket.getCreatedDate());
            if (ticket.getAssignedEngineerId() != null) {
                ps.setInt(9, ticket.getAssignedEngineerId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setString(10, ticket.getStatus().name());
            ps.setString(11, ticket.getSlaDeadline());
            ps.setString(12, ticket.getSlaStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) ticket.setTicketId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(TroubleTicket ticket) {
        String sql = "UPDATE trouble_tickets SET assigned_engineer_id=?, status=?, sla_status=?, " +
                "resolution_date=?, root_cause=?, resolution_details=?, resolution_code=? WHERE ticket_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (ticket.getAssignedEngineerId() != null) {
                ps.setInt(1, ticket.getAssignedEngineerId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, ticket.getStatus().name());
            ps.setString(3, ticket.getSlaStatus().name());
            ps.setString(4, ticket.getResolutionDate());
            ps.setString(5, ticket.getRootCause());
            ps.setString(6, ticket.getResolutionDetails());
            ps.setString(7, ticket.getResolutionCode() != null ? ticket.getResolutionCode().name() : null);
            ps.setInt(8, ticket.getTicketId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // JDBC Transaction Assignment Method implementing PDF page 12/13 requirement
    @Override
    public boolean assignEngineerTransaction(Connection conn, int ticketId, int engineerId, String changedBy) throws SQLException {
        Savepoint savepoint = null;
        try {
            conn.setAutoCommit(false); // Begin Transaction
            savepoint = conn.setSavepoint("BeforeEngineerAssignment");

            // 1. Validate Ticket
            String checkTicketSql = "SELECT status FROM trouble_tickets WHERE ticket_id = ?";
            String oldStatus = null;
            try (PreparedStatement ps = conn.prepareStatement(checkTicketSql)) {
                ps.setInt(1, ticketId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Transaction Aborted: Invalid Ticket ID " + ticketId);
                    }
                    oldStatus = rs.getString("status");
                }
            }

            // 2. Validate Engineer & 3. Check Engineer Availability
            String checkEngSql = "SELECT availability, active_ticket_count FROM network_engineers WHERE engineer_id = ?";
            int currentWorkload = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkEngSql)) {
                ps.setInt(1, engineerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Transaction Aborted: Invalid Engineer ID " + engineerId);
                    }
                    boolean available = rs.getBoolean("availability");
                    if (!available) {
                        throw new SQLException("Transaction Aborted: Engineer " + engineerId + " is not currently available!");
                    }
                    currentWorkload = rs.getInt("active_ticket_count");
                }
            }

            // 4. Assign Engineer & 5. Update Ticket
            String updateTicketSql = "UPDATE trouble_tickets SET assigned_engineer_id = ?, status = 'ASSIGNED' WHERE ticket_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateTicketSql)) {
                ps.setInt(1, engineerId);
                ps.setInt(2, ticketId);
                ps.executeUpdate();
            }

            // Update Engineer Workload
            String updateEngSql = "UPDATE network_engineers SET active_ticket_count = ? WHERE engineer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateEngSql)) {
                ps.setInt(1, currentWorkload + 1);
                ps.setInt(2, engineerId);
                ps.executeUpdate();
            }

            // 6. Create Status History
            String now = DateUtil.now();
            String historySql = "INSERT INTO ticket_status_history (ticket_id, old_status, new_status, changed_by, changed_date, remarks) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(historySql)) {
                ps.setInt(1, ticketId);
                ps.setString(2, oldStatus);
                ps.setString(3, "ASSIGNED");
                ps.setString(4, changedBy);
                ps.setString(5, now);
                ps.setString(6, "Engineer ID " + engineerId + " assigned to ticket.");
                ps.executeUpdate();
            }

            // 7. Create Notification
            String notifSql = "INSERT INTO notifications (recipient_id, message, notification_type, created_date, read_status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(notifSql)) {
                ps.setString(1, String.valueOf(engineerId));
                ps.setString(2, "New trouble ticket #" + ticketId + " has been assigned to you.");
                ps.setString(3, "ENGINEER_ASSIGNMENT");
                ps.setString(4, now);
                ps.setBoolean(5, false);
                ps.executeUpdate();
            }

            // 8. Create Audit Record
            String auditSql = "INSERT INTO audit_logs (action, performed_by, timestamp, details) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setString(1, "ASSIGN_ENGINEER");
                ps.setString(2, changedBy);
                ps.setString(3, now);
                ps.setString(4, "Assigned Ticket ID " + ticketId + " to Engineer ID " + engineerId);
                ps.executeUpdate();
            }

            // 9. COMMIT Transaction
            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            if (conn != null && savepoint != null) {
                conn.rollback(savepoint);
                conn.setAutoCommit(true);
            }
            throw e;
        }
    }

    @Override
    public boolean addStatusHistory(TicketStatusHistory history) {
        String sql = "INSERT INTO ticket_status_history (ticket_id, old_status, new_status, changed_by, changed_date, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, history.getTicketId());
            ps.setString(2, history.getOldStatus() != null ? history.getOldStatus().name() : null);
            ps.setString(3, history.getNewStatus().name());
            ps.setString(4, history.getChangedBy());
            ps.setString(5, history.getChangedDate());
            ps.setString(6, history.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<TicketStatusHistory> getTicketHistory(int ticketId) {
        List<TicketStatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket_status_history WHERE ticket_id = ? ORDER BY history_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TicketStatusHistory(
                            rs.getInt("history_id"),
                            rs.getInt("ticket_id"),
                            rs.getString("old_status") != null ? TicketStatus.valueOf(rs.getString("old_status")) : null,
                            TicketStatus.valueOf(rs.getString("new_status")),
                            rs.getString("changed_by"),
                            rs.getString("changed_date"),
                            rs.getString("remarks")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updatePriority(int ticketId, TicketPriority priority) {
        String sql = "UPDATE trouble_tickets SET priority = ? WHERE ticket_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, priority.name());
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TroubleTicket mapRowToTicket(ResultSet rs) throws SQLException {
        TroubleTicket t = new TroubleTicket();
        t.setTicketId(rs.getInt("ticket_id"));
        t.setTicketNumber(rs.getString("ticket_number"));
        t.setCustomerId(rs.getInt("customer_id"));
        t.setServiceId(rs.getInt("service_id"));
        t.setCategory(IncidentCategory.valueOf(rs.getString("category")));
        t.setDescription(rs.getString("description"));
        t.setPriority(TicketPriority.valueOf(rs.getString("priority")));
        t.setSeverity(rs.getString("severity"));
        t.setCreatedDate(rs.getString("created_date"));
        int engId = rs.getInt("assigned_engineer_id");
        t.setAssignedEngineerId(rs.wasNull() ? null : engId);
        t.setStatus(TicketStatus.valueOf(rs.getString("status")));
        t.setSlaDeadline(rs.getString("sla_deadline"));
        t.setResolutionDate(rs.getString("resolution_date"));
        t.setSlaStatus(SLAStatus.valueOf(rs.getString("sla_status")));
        t.setRootCause(rs.getString("root_cause"));
        t.setResolutionDetails(rs.getString("resolution_details"));
        String resCodeStr = rs.getString("resolution_code");
        if (resCodeStr != null) {
            t.setResolutionCode(ResolutionCode.valueOf(resCodeStr));
        }
        return t;
    }
}
