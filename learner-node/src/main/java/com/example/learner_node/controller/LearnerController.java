package com.example.learner_node.controller;

import com.example.learner_node.service.LearnerService;
import com.example.learner_node.util.WordCountResult;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/learner")
public class LearnerController {
    private final LearnerService learnerService;

    @PostMapping("save")
    public ResponseEntity<String> save(@RequestBody final WordCountResult result) {
        learnerService.addResult(result);
        return ResponseEntity.ok().body("Success");
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(learnerService.getAllResults());
    }
}
