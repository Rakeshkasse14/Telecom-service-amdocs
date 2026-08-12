package com.amdocs.telecom.service;

import com.amdocs.telecom.model.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(Notification notification);
    List<Notification> getNotificationsForUser(String recipientId);
    void markAsRead(int notificationId);
}
