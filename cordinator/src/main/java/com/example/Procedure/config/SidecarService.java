package com.example.Procedure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SidecarService {
    private final Logger LOGGER = LoggerFactory.getLogger(SidecarService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SidecarService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String message) {
        LOGGER.info("Sending broadcast message to kafka (Inside SidecarService): {}", message + " " + topic + " " + new Date());
        kafkaTemplate.send(topic, message);
        LOGGER.info("Sent broadcast message to kafka (Inside SidecarService): {}", message);
    }
}
