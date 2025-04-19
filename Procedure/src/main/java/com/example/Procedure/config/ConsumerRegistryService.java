package com.example.Procedure.config;

import com.example.Procedure.util.LearnerRegisterDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerRegistryService {
    private final Logger LOGGER = LoggerFactory.getLogger(ConsumerRegistryService.class);

    private final ConcurrentHashMap<String, LearnerRegisterDetails> activeConsumers = new ConcurrentHashMap<>();
    private static final long TIMEOUT_SECONDS = 10;
    private final ObjectMapper objectMapper;

    public ConsumerRegistryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${consumer.register.topic}", groupId = "${consumer.register.group}")
    public void listenConsumerRegistry(String jsonMessage) {
        try {
            LearnerRegisterDetails registerDetails = objectMapper.readValue(jsonMessage, LearnerRegisterDetails.class);
            updateConsumerMap(registerDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void updateConsumerMap(LearnerRegisterDetails registerDetails) {
        activeConsumers.put(registerDetails.getNodeId(), registerDetails);
        LOGGER.info("Registered/Updated consumer: {}", registerDetails.getNodeId());
    }

    public List<String> getActiveConsumers() {
        return new ArrayList<>(activeConsumers.keySet());
    }

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void removeTimedOutConsumers() {
        long now = System.currentTimeMillis();

        activeConsumers.forEach((nodeId, details) -> {
            long lastSeen = details.getDate().getTime();
            if ((now - lastSeen) > TIMEOUT_SECONDS * 1000) {
                activeConsumers.remove(nodeId);
                LOGGER.warn("Removed timed-out consumer: {}", nodeId);
            }
        });
    }
}
