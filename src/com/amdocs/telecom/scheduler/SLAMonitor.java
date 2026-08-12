package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.enums.SLAStatus;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.service.SLAService;
import com.amdocs.telecom.service.TicketService;
import com.amdocs.telecom.service.impl.SLAServiceImpl;
import com.amdocs.telecom.service.impl.TicketServiceImpl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically monitors active trouble tickets using ScheduledExecutorService to detect SLA warnings & breaches.
 */
public class SLAMonitor {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TicketService ticketService = new TicketServiceImpl();
    private final SLAService slaService = new SLAServiceImpl();

    public void startMonitoring() {
        System.out.println("[SCHEDULER] SLA Monitor Service started. Checking every 15 seconds...");
        scheduler.scheduleAtFixedRate(this::checkTickets, 5, 15, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
    }

    private void checkTickets() {
        try {
            List<TroubleTicket> openTickets = ticketService.getOpenTickets();
            for (TroubleTicket ticket : openTickets) {
                SLAStatus oldStatus = ticket.getSlaStatus();
                slaService.updateTicketSLAStatus(ticket);
                if (oldStatus != ticket.getSlaStatus()) {
                    if (ticket.getSlaStatus() == SLAStatus.AT_RISK) {
                        System.out.println("[SLA WARNING] Ticket #" + ticket.getTicketNumber() + " is AT RISK! Deadline: " + ticket.getSlaDeadline());
                    } else if (ticket.getSlaStatus() == SLAStatus.BREACHED) {
                        System.out.println("[SLA BREACH ALERT] Ticket #" + ticket.getTicketNumber() + " has BREACHED SLA deadline!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SLA MONITOR ERROR] " + e.getMessage());
        }
    }
}
