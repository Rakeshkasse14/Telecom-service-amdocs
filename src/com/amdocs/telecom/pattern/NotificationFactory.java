package com.amdocs.telecom.pattern;

import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.util.DateUtil;

public class NotificationFactory {

    public static Notification createNotification(String recipientId, String type, String message) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setNotificationType(type);
        notification.setMessage(message);
        notification.setCreatedDate(DateUtil.now());
        notification.setReadStatus(false);
        return notification;
    }

    public static Notification createTicketCreationNotification(String recipientId, String ticketNumber) {
        return createNotification(recipientId, "TICKET_CREATION", 
                "Your trouble ticket " + ticketNumber + " has been successfully created and queued for dispatch.");
    }

    public static Notification createEngineerAssignmentNotification(String recipientId, String ticketNumber, String engineerName) {
        return createNotification(recipientId, "ENGINEER_ASSIGNMENT", 
                "Ticket " + ticketNumber + " has been assigned to Network Engineer: " + engineerName + ".");
    }

    public static Notification createSlaWarningNotification(String recipientId, String ticketNumber, long minutesLeft) {
        return createNotification(recipientId, "SLA_WARNING", 
                "WARNING: Ticket " + ticketNumber + " is AT RISK! Only " + minutesLeft + " minutes remaining in SLA.");
    }

    public static Notification createSlaBreachNotification(String recipientId, String ticketNumber) {
        return createNotification(recipientId, "SLA_BREACH", 
                "CRITICAL ALERT: Ticket " + ticketNumber + " has BREACHED SLA! Immediate escalation triggered.");
    }
}
