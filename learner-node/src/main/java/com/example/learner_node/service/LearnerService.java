package com.example.learner_node.service;

import com.example.learner_node.util.WordCountResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LearnerService {

    private final Logger LOGGER = LoggerFactory.getLogger(LearnerService.class);

    private final Map<String, List<WordCountResult>> results = new HashMap<>();

    public void addResult(WordCountResult result) {
        LOGGER.info("Adding result: {}", result);

        final String processId = result.getProcessId();
        results.computeIfAbsent(processId, k -> new ArrayList<>()).add(result);

        LOGGER.info("Added result: {}", result);
    }

    public Map<String, List<WordCountResult>> getAllResults() {
        LOGGER.info("Getting all results");

        return results;
    }
}
