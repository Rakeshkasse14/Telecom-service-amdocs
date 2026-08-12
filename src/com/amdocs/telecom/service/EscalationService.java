package com.amdocs.telecom.service;

import com.amdocs.telecom.model.EscalationHistory;
import com.amdocs.telecom.model.TroubleTicket;

import java.util.List;
import java.util.PriorityQueue;

public interface EscalationService {
    void queueTicketForEscalation(TroubleTicket ticket);
    TroubleTicket processNextEscalation(String escalatedBy, String reason);
    PriorityQueue<TroubleTicket> getEscalationQueue();
    List<EscalationHistory> getEscalationHistory(int ticketId);
}
