package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.entity.event.EventMessage;

@Service
public class KafkaNotificationService implements NotificationService {
    private final KafkaTemplate<String, EventMessage> kafkaTemplate;
    private final String topicName;

    @Autowired
    public KafkaNotificationService(KafkaTemplate<String, EventMessage> kafkaTemplate, @Value("${spring.kafka.notifications-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void sendNotification(EventMessage eventMessage) {
        if (eventMessage.getUsersUUID().isEmpty())
            return;
        kafkaTemplate.send(topicName, eventMessage);
    }
}
