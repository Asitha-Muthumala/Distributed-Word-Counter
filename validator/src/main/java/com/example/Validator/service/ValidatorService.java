package com.example.Validator.service;

import com.example.Validator.config.FiegnConfig;
import com.example.Validator.util.WordCountResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService {
    final static Logger LOGGER = LoggerFactory.getLogger(ValidatorService.class);
    private final FiegnConfig fiegnConfig;

    public ValidatorService(FiegnConfig fiegnConfig) {
        this.fiegnConfig = fiegnConfig;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void validateAndStore(String json) throws JsonProcessingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WordCountResult result = mapper.readValue(json, WordCountResult.class);

        if (isValidResult(result)) {
            LOGGER.info("Send Result to Learner Node. {}", result);
            fiegnConfig.sendData(result);
        } else {
            LOGGER.warn("Invalid result : {}", result);
        }
    }

    private boolean isValidResult(WordCountResult result) {
        char start = Character.toLowerCase(result.getStart());
        char end = Character.toLowerCase(result.getEnd());

        for (String word : result.getWords()) {
            if (word == null || word.isEmpty()) {
                return false;
            }

            char first = Character.toLowerCase(word.charAt(0));
            if (first < start || first > end) {
                return false;
            }
        }

        return result.getCount() == result.getWords().size();
    }
}
