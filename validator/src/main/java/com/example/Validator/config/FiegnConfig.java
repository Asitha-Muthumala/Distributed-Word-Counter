package com.example.Validator.config;

import com.example.Validator.util.WordCountResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "learner-node", url = "http://localhost:8084")
public interface FiegnConfig {
    @PostMapping("/api/learner/save")
    void sendData(@RequestBody WordCountResult result);
}
