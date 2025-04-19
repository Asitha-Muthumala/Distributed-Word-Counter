package com.example.Consumer2.controller;

import com.example.Consumer2.config.LeaderElectionService;
import com.example.Consumer2.service.abstraction.CordinatorService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/cordinator")
public class CordinatorController {
    private final static Logger LOGGER = LoggerFactory.getLogger(CordinatorController.class);
    private final CordinatorService cordinatorService;
    private final LeaderElectionService leaderElectionService;

    @PostMapping("/broadcast")
    public ResponseEntity<String> Broadcast(@RequestParam String input) {
        LOGGER.info("Requesting - Controller: {}", input);

        if (!leaderElectionService.isLeader()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only the leader can process requests!");
        }

        cordinatorService.BroadcastInput(input);

        LOGGER.info("Broadcasting done - Controller");
        return ResponseEntity.ok().body("Successfully broadcast");
    }
}
