package com.amdocs.telecom.pattern;

import com.amdocs.telecom.model.TroubleTicket;
import java.util.ArrayList;
import java.util.List;

public class TicketSubject {
    private final List<TicketObserver> observers = new ArrayList<>();

    public void registerObserver(TicketObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TicketObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(TroubleTicket ticket, String oldStatus, String newStatus, String remarks) {
        for (TicketObserver observer : observers) {
            observer.onTicketStatusChanged(ticket, oldStatus, newStatus, remarks);
        }
    }
}
