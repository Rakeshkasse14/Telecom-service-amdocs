package com.amdocs.telecom.service;

import com.amdocs.telecom.enums.SLAStatus;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.model.TroubleTicket;

public interface SLAService {
    String calculateSLADeadline(TicketPriority priority);
    SLAStatus evaluateSLAStatus(String slaDeadlineStr, boolean isResolved);
    long getRemainingTimeMinutes(String slaDeadlineStr);
    void updateTicketSLAStatus(TroubleTicket ticket);
}
