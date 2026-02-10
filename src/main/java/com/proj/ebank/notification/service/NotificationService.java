package com.proj.ebank.notification.service;

import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.users.entity.User;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
}
