package com.amdocs.telecom.pattern;

import com.amdocs.telecom.model.TroubleTicket;

public interface TicketObserver {
    void onTicketStatusChanged(TroubleTicket ticket, String oldStatus, String newStatus, String remarks);
}
