package com.amdocs.telecom.pattern;

import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.model.TroubleTicket;

public class NotificationObserver implements TicketObserver {

    @Override
    public void onTicketStatusChanged(TroubleTicket ticket, String oldStatus, String newStatus, String remarks) {
        String msg = String.format("Ticket #%s status updated from %s to %s. Remarks: %s",
                ticket.getTicketNumber(), oldStatus, newStatus, remarks);
        Notification notification = NotificationFactory.createNotification(
                String.valueOf(ticket.getCustomerId()), "STATUS_CHANGE", msg);
        System.out.println("[OBSERVER NOTIFICATION DISPATCHED] " + notification.getMessage());
    }
}
