package ru.t1.debut.muse.service;

import org.springframework.stereotype.Service;
import ru.t1.debut.muse.entity.EventMessage;

@Service
public class KafkaNotificationService implements NotificationService {
    @Override
    public void sendNotification(EventMessage eventMessage) {
        System.out.println(eventMessage);
    }
}
