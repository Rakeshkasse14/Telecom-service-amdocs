package com.amdocs.telecom.service;

import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.TicketCreateDTO;
import com.amdocs.telecom.dto.TicketResolutionDTO;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.exception.TicketNotFoundException;
import com.amdocs.telecom.model.TroubleTicket;

import java.util.List;

public interface TicketService {
    TroubleTicket createTicket(TicketCreateDTO dto);
    TroubleTicket getTicketById(int ticketId) throws TicketNotFoundException;
    TroubleTicket getTicketByNumber(String ticketNumber) throws TicketNotFoundException;
    List<TroubleTicket> getCustomerTickets(int customerId);
    List<TroubleTicket> getEngineerTickets(int engineerId);
    List<TroubleTicket> getOpenTickets();
    List<TroubleTicket> getAllTickets();
    boolean resolveTicket(TicketResolutionDTO dto) throws TicketNotFoundException;
    boolean closeTicket(int ticketId, String closedBy, String remarks) throws TicketNotFoundException;
    boolean updatePriority(int ticketId, TicketPriority priority) throws TicketNotFoundException;
    DashboardMetricsDTO getDashboardMetrics();
}
