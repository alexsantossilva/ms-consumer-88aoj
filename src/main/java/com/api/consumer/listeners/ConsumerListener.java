package com.api.consumer.listeners;

import com.api.consumer.services.WebhookTeamsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ConsumerListener {

    private final WebhookTeamsService webhookTeamsService;

    public ConsumerListener(WebhookTeamsService webhookTeamsService) {
        this.webhookTeamsService = webhookTeamsService;
    }

    @KafkaListener(topics = "${topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumer(String message) throws IOException {
        System.out.println("[X] Message: " + message);
        webhookTeamsService.sendNotification(message);
    }
}
