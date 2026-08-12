package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.Notification;
import java.util.List;

public interface NotificationDAO {
    boolean save(Notification notification);
    List<Notification> findByRecipientId(String recipientId);
    boolean markAsRead(int notificationId);
}
