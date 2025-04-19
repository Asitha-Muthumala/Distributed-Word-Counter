package com.example.Procedure.service.impl;

import com.example.Procedure.config.SidecarService;
import com.example.Procedure.service.abstraction.CordinatorService;
import com.example.Procedure.util.NodeRange;
import com.example.Procedure.util.ParagraphMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CordinatorServiceImpl implements CordinatorService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CordinatorServiceImpl.class);

    @Value("${kafka.topic}")
    private String topic;

    private final ConsumerRegistryService consumerRegistryService;
    private final SidecarService sidecarService;

    public CordinatorServiceImpl(ConsumerRegistryService consumerRegistryService, SidecarService sidecarService) {
        this.consumerRegistryService = consumerRegistryService;
        this.sidecarService = sidecarService;
    }

    @Override
    public void BroadcastInput(String input) {
        LOGGER.info("Received Broadcast Input - Service: {}", input);

        final String uuid = UUID.randomUUID().toString();
        final List<NodeRange> ranges = assignLetterRangesToNodes(consumerRegistryService.getActiveConsumers());
        final ParagraphMessage paragraphMessage = setMessage(input, uuid, ranges);

        try {
            final String json = new ObjectMapper().writeValueAsString(paragraphMessage);
            sidecarService.send(topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Broadcasting done - Service");
    }

    private ParagraphMessage setMessage(String message, String uuid, List<NodeRange> ranges) {
        return ParagraphMessage.builder()
                .processId(uuid)
                .paragraph(message)
                .ranges(ranges)
                .build();
    }

    public List<NodeRange> assignLetterRangesToNodes(List<String> nodeIds) {
        int totalNodes = nodeIds.size();
        List<NodeRange> assignments = new ArrayList<>();

        if (totalNodes == 0) return assignments;

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int totalLetters = alphabet.length;

        int baseSize = totalLetters / totalNodes;
        int remainder = totalLetters % totalNodes;

        int currentIndex = 0;

        for (int i = 0; i < totalNodes; i++) {
            int size = baseSize + (i < remainder ? 1 : 0);

            if (currentIndex >= totalLetters) {
                assignments.add(new NodeRange(nodeIds.get(i), '-', '-'));
                continue;
            }

            char start = alphabet[currentIndex];
            char end = alphabet[Math.min(currentIndex + size - 1, totalLetters - 1)];

            assignments.add(NodeRange.builder()
                    .nodeId(nodeIds.get(i))
                    .start(start)
                    .end(end)
                    .build());

            currentIndex += size;
        }

        return assignments;
    }

}
