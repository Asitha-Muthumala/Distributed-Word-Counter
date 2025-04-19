package com.example.Procedure.controller;

import com.example.Procedure.config.LeaderElectionService;
import com.example.Procedure.service.abstraction.CordinatorService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
