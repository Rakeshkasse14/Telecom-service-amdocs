package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.enums.SLAStatus;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.enums.TicketStatus;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.pattern.DefaultSLAStrategy;
import com.amdocs.telecom.pattern.SLAStrategy;
import com.amdocs.telecom.service.SLAService;
import com.amdocs.telecom.util.DateUtil;

public class SLAServiceImpl implements SLAService {

    private final SLAStrategy slaStrategy = new DefaultSLAStrategy();

    @Override
    public String calculateSLADeadline(TicketPriority priority) {
        int hours = slaStrategy.getResolutionSlaHours(priority);
        return DateUtil.calculateDeadline(hours);
    }

    @Override
    public SLAStatus evaluateSLAStatus(String slaDeadlineStr, boolean isResolved) {
        if (isResolved) {
            return SLAStatus.WITHIN_SLA;
        }
        long minutesLeft = DateUtil.minutesRemaining(slaDeadlineStr);
        if (minutesLeft < 0) {
            return SLAStatus.BREACHED;
        } else if (minutesLeft <= 30) {
            return SLAStatus.AT_RISK;
        } else {
            return SLAStatus.WITHIN_SLA;
        }
    }

    @Override
    public long getRemainingTimeMinutes(String slaDeadlineStr) {
        return DateUtil.minutesRemaining(slaDeadlineStr);
    }

    @Override
    public void updateTicketSLAStatus(TroubleTicket ticket) {
        boolean isResolved = (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED);
        SLAStatus newStatus = evaluateSLAStatus(ticket.getSlaDeadline(), isResolved);
        ticket.setSlaStatus(newStatus);
    }
}
