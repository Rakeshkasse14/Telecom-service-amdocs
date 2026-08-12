package com.amdocs.telecom.model;

public class Notification {
    private int notificationId;
    private String recipientId;
    private String message;
    private String notificationType;
    private String createdDate;
    private boolean readStatus;

    public Notification() {}

    public Notification(int notificationId, String recipientId, String message,
                        String notificationType, String createdDate, boolean readStatus) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.message = message;
        this.notificationType = notificationType;
        this.createdDate = createdDate;
        this.readStatus = readStatus;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
}
