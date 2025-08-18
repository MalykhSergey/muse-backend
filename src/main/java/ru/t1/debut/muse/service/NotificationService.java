package ru.t1.debut.muse.service;

import ru.t1.debut.muse.entity.EventMessage;

public interface NotificationService {
    void sendNotification(EventMessage eventMessage);
}
