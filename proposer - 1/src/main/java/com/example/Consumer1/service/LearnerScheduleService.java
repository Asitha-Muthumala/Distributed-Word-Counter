package com.example.Consumer1.service;

import com.example.Consumer1.config.SidecarService;
import com.example.Consumer1.util.LearnerRegisterDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LearnerScheduleService {

    private final Logger LOGGER = LoggerFactory.getLogger(SidecarService.class);
    private static final String REGISTRY_TOPIC = "ds-consumer-register";

    @Value("${consumer.node-id}")
    private String nodeId;

    private final SidecarService sidecarService;
    private final ObjectMapper objectMapper;

    public LearnerScheduleService(SidecarService sidecarService, ObjectMapper objectMapper) {
        this.sidecarService = sidecarService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void registerConsumer() {
        consumerRegister();
        LOGGER.info("Consumer registered with nodeId: {}", nodeId);
    }

    @Scheduled(fixedRate = 10000)
    public void sendHeartbeat() {
        consumerRegister();
        LOGGER.info("Heartbeat sent for nodeId: {}", nodeId);
    }

    private void consumerRegister() {
        final LearnerRegisterDetails details = LearnerRegisterDetails.builder()
                .nodeId(nodeId)
                .date(new Date())
                .build();

        try {
            final String consumerDetails = objectMapper.writeValueAsString(details);
            sidecarService.send(REGISTRY_TOPIC, consumerDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

