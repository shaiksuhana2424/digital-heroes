package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.Winner;
import com.digitalheroes.digital_heroes_backend.service.WinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/winners")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
        }
)
public class WinnerController {

    private final WinnerService winnerService;

    public WinnerController(WinnerService winnerService) {
        this.winnerService = winnerService;
    }

    @GetMapping
    public ResponseEntity<?> getUserWinners(
            @RequestParam String email
    ) {

        try {

            List<Winner> winners =
                    winnerService.getWinnersForUser(email);

            return ResponseEntity.ok(winners);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWinners() {

        try {

            return ResponseEntity.ok(
                    winnerService.getAllWinners()
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingWinners() {

        try {

            return ResponseEntity.ok(
                    winnerService.getPendingVerification()
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping(
            value = "/{winnerId}/proof",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> uploadProof(
            @PathVariable Long winnerId,
            @RequestParam String email,
            @RequestParam("file") MultipartFile file
    ) {

        try {

            return ResponseEntity.ok(
                    winnerService.uploadProof(
                            winnerId,
                            email,
                            file
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{winnerId}/approve")
    public ResponseEntity<?> approveWinner(
            @PathVariable Long winnerId
    ) {

        try {

            return ResponseEntity.ok(
                    winnerService.approveWinner(winnerId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{winnerId}/reject")
    public ResponseEntity<?> rejectWinner(
            @PathVariable Long winnerId,
            @RequestParam(required = false) String reason
    ) {

        try {

            return ResponseEntity.ok(
                    winnerService.rejectWinner(
                            winnerId,
                            reason
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{winnerId}/paid")
    public ResponseEntity<?> markAsPaid(
            @PathVariable Long winnerId
    ) {

        try {

            return ResponseEntity.ok(
                    winnerService.markAsPaid(winnerId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}