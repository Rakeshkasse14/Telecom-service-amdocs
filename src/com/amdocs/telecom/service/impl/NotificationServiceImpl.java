package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.dao.impl.NotificationDAOImpl;
import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.service.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public void sendNotification(Notification notification) {
        notificationDAO.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(String recipientId) {
        return notificationDAO.findByRecipientId(recipientId);
    }

    @Override
    public void markAsRead(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }
}
