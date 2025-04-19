package com.example.Consumer2.service;

import com.example.Consumer2.config.LeaderElectionService;
import com.example.Consumer2.config.SidecarService;
import com.example.Consumer2.util.NodeRange;
import com.example.Consumer2.util.ParagraphMessage;
import com.example.Consumer2.util.WordCountResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class LearnerService {
    final static Logger LOGGER = LoggerFactory.getLogger(LearnerService.class);
    private final LeaderElectionService leaderElectionService;

    @Value("${kafka.topic}")
    private String topic;
    @Value("${validator.kafka.topic}")
    private String validatorTopic;
    @Value("${spring.kafka.consumer.group-id}")
    private String group;
    @Value("${consumer.node-id}")
    private String nodeId;

    private final SidecarService sidecarService;

    public LearnerService(LeaderElectionService leaderElectionService, SidecarService sidecarService) {
        this.leaderElectionService = leaderElectionService;
        this.sidecarService = sidecarService;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void LearnerOneListen(String message) throws JsonProcessingException {
        if (leaderElectionService.isLeader()) {
            LOGGER.info("Leader has not permission to process message");
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ParagraphMessage msg = mapper.readValue(message, ParagraphMessage.class);

            NodeRange myRange = msg.getRanges().stream()
                    .filter(r -> r.getNodeId().equalsIgnoreCase(nodeId))
                    .findFirst()
                    .orElse(null);

            if (myRange != null) {
                List<String> filterWord = Arrays.stream(msg.getParagraph().split(" "))
                        .filter(s -> !s.isEmpty())
                        .filter(s -> {
                            char ch = Character.toLowerCase(s.charAt(0));
                            return ch >= myRange.getStart() && ch <= myRange.getEnd();
                        })
                        .toList();

                final WordCountResult result = setWordCountResult(msg.getProcessId(), myRange.getStart(),
                        myRange.getEnd(), filterWord);
                sendToValidator(result);
            } else {
                LOGGER.info("Node not found");
            }
        }
    }

    private void sendToValidator(WordCountResult result) {
        LOGGER.info("Sending result to validator topic: {}", result);
        try {
            final String json = new ObjectMapper().writeValueAsString(result);
            sidecarService.send(validatorTopic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private WordCountResult setWordCountResult(String processId, char start, char end, List<String> words) {
        return WordCountResult.builder()
                .processId(processId)
                .start(start)
                .end(end)
                .words(words)
                .count(words.size())
                .build();
    }
}
