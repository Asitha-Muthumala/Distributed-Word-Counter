package com.example.Consumer2.config;

import com.example.Consumer2.util.NodeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LeaderElectionService {
    private final Logger LOGGER = LoggerFactory.getLogger(LeaderElectionService.class);
    private final Map<String, NodeInfo> nodes = new ConcurrentHashMap<>();
    private final SidecarService sidecarService;
    private String currentLeaderId = null;

    @Value("${kafka.leader.elect.topic}")
    private String leaderElectTopic;
    @Value("${node.id}")
    private String nodeId;
    @Value("${node.weight}")
    private String nodeWeight;

    public LeaderElectionService(SidecarService sidecarService) {
        this.sidecarService = sidecarService;
    }

    @Scheduled(fixedRate = 3000) // Send heartbeat every 3 sec
    public void sendHeartbeat() {
        LOGGER.info("Sending Heartbeat - LeaderElectionService: {}", leaderElectTopic);
        NodeInfo info = NodeInfo.builder()
                .nodeId(nodeId)
                .weight(nodeWeight)
                .lastHeartbeat(System.currentTimeMillis())
                .build();

        try {
            final String json = new ObjectMapper().writeValueAsString(info);
            sidecarService.send(leaderElectTopic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Heartbeat sent");
    }

    @KafkaListener(topics = "${kafka.leader.elect.topic}", groupId = "${kafka.leader.elect.group}")
    public void receiveHeartbeat(String jsonMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            NodeInfo nodeInfo = objectMapper.readValue(jsonMessage, NodeInfo.class);

            nodes.put(nodeInfo.getNodeId(), nodeInfo);
            electLeader();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void electLeader() {
        long now = System.currentTimeMillis();

        List<String> deadNodes = new ArrayList<>();
        for (Map.Entry<String, NodeInfo> entry : nodes.entrySet()) {
            NodeInfo nodeInfo = entry.getValue();
            if (now - nodeInfo.getLastHeartbeat() > 10000) {
                deadNodes.add(entry.getKey());
            }
        }
        for (String deadNodeId : deadNodes) {
            nodes.remove(deadNodeId);
        }

        String leaderId = null;
        int maxWeight = Integer.MIN_VALUE;
        for (NodeInfo nodeInfo : nodes.values()) {
            if (Integer.parseInt(nodeInfo.getWeight()) > maxWeight) {
                maxWeight = Integer.parseInt(nodeInfo.getWeight());
                leaderId = nodeInfo.getNodeId();
            }
        }

        this.currentLeaderId = leaderId;
    }

    public boolean isLeader() {
        return nodeId.equals(currentLeaderId);
    }
}
