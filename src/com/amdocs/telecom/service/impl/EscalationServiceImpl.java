package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.TicketDAO;
import com.amdocs.telecom.dao.impl.TicketDAOImpl;
import com.amdocs.telecom.enums.TicketStatus;
import com.amdocs.telecom.model.EscalationHistory;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.service.EscalationService;
import com.amdocs.telecom.util.DBConnection;
import com.amdocs.telecom.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class EscalationServiceImpl implements EscalationService {

    // PDF Page 7 Requirement: PriorityQueue ensuring CRITICAL tickets are processed before lower priority tickets
    private final PriorityQueue<TroubleTicket> escalationQueue = new PriorityQueue<>();
    private final TicketDAO ticketDAO = new TicketDAOImpl();

    @Override
    public synchronized void queueTicketForEscalation(TroubleTicket ticket) {
        if (!escalationQueue.contains(ticket)) {
            escalationQueue.add(ticket);
            System.out.println("[ESCALATION QUEUE] Ticket #" + ticket.getTicketNumber() + " (" + ticket.getPriority() + ") added to PriorityQueue.");
        }
    }

    @Override
    public synchronized TroubleTicket processNextEscalation(String escalatedBy, String reason) {
        TroubleTicket ticket = escalationQueue.poll(); // Dequeues highest priority ticket
        if (ticket == null) {
            return null;
        }

        String fromLevel = resolveCurrentLevel(ticket);
        String toLevel = getNextLevel(fromLevel);

        ticket.setStatus(TicketStatus.ESCALATED);
        ticketDAO.update(ticket);

        // Record Escalation History
        recordEscalation(ticket.getTicketId(), fromLevel, toLevel, reason, escalatedBy);
        System.out.printf(">> Ticket #%s ESCALATED from [%s] to [%s] by %s. Reason: %s\n",
                ticket.getTicketNumber(), fromLevel, toLevel, escalatedBy, reason);

        return ticket;
    }

    @Override
    public PriorityQueue<TroubleTicket> getEscalationQueue() {
        return escalationQueue;
    }

    @Override
    public List<EscalationHistory> getEscalationHistory(int ticketId) {
        List<EscalationHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM escalation_history WHERE ticket_id = ? ORDER BY escalation_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EscalationHistory(
                            rs.getInt("escalation_id"),
                            rs.getInt("ticket_id"),
                            rs.getString("from_level"),
                            rs.getString("to_level"),
                            rs.getString("reason"),
                            rs.getString("escalation_date"),
                            rs.getString("escalated_by")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void recordEscalation(int ticketId, String fromLevel, String toLevel, String reason, String escalatedBy) {
        String sql = "INSERT INTO escalation_history (ticket_id, from_level, to_level, reason, escalation_date, escalated_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            ps.setString(2, fromLevel);
            ps.setString(3, toLevel);
            ps.setString(4, reason);
            ps.setString(5, DateUtil.now());
            ps.setString(6, escalatedBy);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String resolveCurrentLevel(TroubleTicket ticket) {
        if (ticket.getStatus() == TicketStatus.ESCALATED) {
            return "Team Lead";
        }
        return "Network Engineer";
    }

    private String getNextLevel(String currentLevel) {
        switch (currentLevel) {
            case "Network Engineer": return "Team Lead";
            case "Team Lead": return "Network Manager";
            case "Network Manager": return "Operations Manager";
            default: return "Operations Manager";
        }
    }
}
