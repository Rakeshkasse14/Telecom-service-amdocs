package com.amdocs.telecom.dao;

import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.enums.TicketStatus;
import com.amdocs.telecom.model.TicketStatusHistory;
import com.amdocs.telecom.model.TroubleTicket;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TicketDAO {
    TroubleTicket findById(int ticketId);
    TroubleTicket findByTicketNumber(String ticketNumber);
    List<TroubleTicket> findByCustomerId(int customerId);
    List<TroubleTicket> findByEngineerId(int engineerId);
    List<TroubleTicket> findAll();
    List<TroubleTicket> findByStatus(TicketStatus status);
    boolean save(TroubleTicket ticket);
    boolean update(TroubleTicket ticket);
    
    // Transactional Assignment Method (Uses Connection context for ACID transaction)
    boolean assignEngineerTransaction(Connection conn, int ticketId, int engineerId, String changedBy) throws SQLException;
    
    boolean addStatusHistory(TicketStatusHistory history);
    List<TicketStatusHistory> getTicketHistory(int ticketId);
    boolean updatePriority(int ticketId, TicketPriority priority);
}
