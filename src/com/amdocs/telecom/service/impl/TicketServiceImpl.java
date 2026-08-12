package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.TicketDAO;
import com.amdocs.telecom.dao.impl.TicketDAOImpl;
import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.TicketCreateDTO;
import com.amdocs.telecom.dto.TicketResolutionDTO;
import com.amdocs.telecom.enums.SLAStatus;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.enums.TicketStatus;
import com.amdocs.telecom.exception.TicketNotFoundException;
import com.amdocs.telecom.model.TicketStatusHistory;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.pattern.NotificationObserver;
import com.amdocs.telecom.pattern.TicketSubject;
import com.amdocs.telecom.service.SLAService;
import com.amdocs.telecom.service.TicketService;
import com.amdocs.telecom.util.DateUtil;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {

    private final TicketDAO ticketDAO = new TicketDAOImpl();
    private final SLAService slaService = new SLAServiceImpl();
    private final TicketSubject ticketSubject = new TicketSubject();
    private final Random random = new Random();

    public TicketServiceImpl() {
        // Register Observer Pattern
        ticketSubject.registerObserver(new NotificationObserver());
    }

    @Override
    public TroubleTicket createTicket(TicketCreateDTO dto) {
        String ticketNumber = "TT-2026-" + String.format("%06d", random.nextInt(900000) + 100000);
        String now = DateUtil.now();
        String slaDeadline = slaService.calculateSLADeadline(dto.getPriority());

        TroubleTicket ticket = new TroubleTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setCustomerId(dto.getCustomerId());
        ticket.setServiceId(dto.getServiceId());
        ticket.setCategory(dto.getCategory());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setSeverity(dto.getSeverity());
        ticket.setCreatedDate(now);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setSlaDeadline(slaDeadline);
        ticket.setSlaStatus(SLAStatus.WITHIN_SLA);

        ticketDAO.save(ticket);

        // Record Initial History
        TicketStatusHistory history = new TicketStatusHistory(0, ticket.getTicketId(), null,
                TicketStatus.OPEN, "CUSTOMER_" + dto.getCustomerId(), now, "Ticket created by customer.");
        ticketDAO.addStatusHistory(history);

        ticketSubject.notifyObservers(ticket, "NONE", "OPEN", "New trouble ticket created.");
        return ticket;
    }

    @Override
    public TroubleTicket getTicketById(int ticketId) throws TicketNotFoundException {
        TroubleTicket t = ticketDAO.findById(ticketId);
        if (t == null) throw new TicketNotFoundException("Ticket ID " + ticketId + " not found.");
        slaService.updateTicketSLAStatus(t);
        return t;
    }

    @Override
    public TroubleTicket getTicketByNumber(String ticketNumber) throws TicketNotFoundException {
        TroubleTicket t = ticketDAO.findByTicketNumber(ticketNumber);
        if (t == null) throw new TicketNotFoundException("Ticket Number " + ticketNumber + " not found.");
        slaService.updateTicketSLAStatus(t);
        return t;
    }

    @Override
    public List<TroubleTicket> getCustomerTickets(int customerId) {
        List<TroubleTicket> list = ticketDAO.findByCustomerId(customerId);
        list.forEach(slaService::updateTicketSLAStatus);
        return list;
    }

    @Override
    public List<TroubleTicket> getEngineerTickets(int engineerId) {
        List<TroubleTicket> list = ticketDAO.findByEngineerId(engineerId);
        list.forEach(slaService::updateTicketSLAStatus);
        return list;
    }

    @Override
    public List<TroubleTicket> getOpenTickets() {
        return getAllTickets().stream()
                .filter(t -> t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED && t.getStatus() != TicketStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    @Override
    public List<TroubleTicket> getAllTickets() {
        List<TroubleTicket> list = ticketDAO.findAll();
        list.forEach(slaService::updateTicketSLAStatus);
        return list;
    }

    @Override
    public boolean resolveTicket(TicketResolutionDTO dto) throws TicketNotFoundException {
        TroubleTicket ticket = getTicketById(dto.getTicketId());
        TicketStatus oldStatus = ticket.getStatus();

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolutionDate(DateUtil.now());
        ticket.setRootCause(dto.getRootCause());
        ticket.setResolutionDetails(dto.getResolutionDetails());
        ticket.setResolutionCode(dto.getResolutionCode());

        boolean updated = ticketDAO.update(ticket);
        if (updated) {
            TicketStatusHistory history = new TicketStatusHistory(0, ticket.getTicketId(), oldStatus,
                    TicketStatus.RESOLVED, dto.getEngineerCode(), DateUtil.now(), "Incident resolved by engineer.");
            ticketDAO.addStatusHistory(history);
            ticketSubject.notifyObservers(ticket, oldStatus.name(), "RESOLVED", dto.getResolutionDetails());
        }
        return updated;
    }

    @Override
    public boolean closeTicket(int ticketId, String closedBy, String remarks) throws TicketNotFoundException {
        TroubleTicket ticket = getTicketById(ticketId);
        TicketStatus oldStatus = ticket.getStatus();

        ticket.setStatus(TicketStatus.CLOSED);
        boolean updated = ticketDAO.update(ticket);
        if (updated) {
            TicketStatusHistory history = new TicketStatusHistory(0, ticket.getTicketId(), oldStatus,
                    TicketStatus.CLOSED, closedBy, DateUtil.now(), remarks);
            ticketDAO.addStatusHistory(history);
            ticketSubject.notifyObservers(ticket, oldStatus.name(), "CLOSED", remarks);
        }
        return updated;
    }

    @Override
    public boolean updatePriority(int ticketId, TicketPriority priority) throws TicketNotFoundException {
        TroubleTicket t = getTicketById(ticketId);
        t.setPriority(priority);
        t.setSlaDeadline(slaService.calculateSLADeadline(priority));
        return ticketDAO.updatePriority(ticketId, priority);
    }

    /**
     * Implements PDF Page 10 Network Manager Dashboard Metrics using Java 8 Streams
     */
    @Override
    public DashboardMetricsDTO getDashboardMetrics() {
        List<TroubleTicket> all = getAllTickets();

        int openCount = (int) all.stream().filter(t -> t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED && t.getStatus() != TicketStatus.CANCELLED).count();
        int criticalCount = (int) all.stream().filter(t -> t.getPriority() == TicketPriority.CRITICAL && t.getStatus() != TicketStatus.CLOSED).count();
        int atRiskCount = (int) all.stream().filter(t -> t.getSlaStatus() == SLAStatus.AT_RISK).count();
        int breachedCount = (int) all.stream().filter(t -> t.getSlaStatus() == SLAStatus.BREACHED).count();
        int resolvedToday = (int) all.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED).count();

        return new DashboardMetricsDTO(openCount, criticalCount, atRiskCount, breachedCount, resolvedToday, 3.8);
    }
}
