package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.Score;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.service.AuthService;
import com.digitalheroes.digital_heroes_backend.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class ScoreController {

    private final ScoreService scoreService;
    private final AuthService authService;

    public ScoreController(
            ScoreService scoreService,
            AuthService authService) {
        this.scoreService = scoreService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getScores(
            @RequestParam String email) {

        try {
            User user = authService.findByEmail(email);

            List<Score> scores =
                    scoreService.getLatestScores(user);

            return ResponseEntity.ok(
                    scores.stream()
                            .map(score -> new ScoreResponse(
                                    score.getId(),
                                    score.getStablefordScore(),
                                    score.getScoreDate()
                            ))
                            .toList()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addScore(
            @RequestParam String email,
            @RequestBody ScoreRequest request) {

        try {
            User user = authService.findByEmail(email);

            Score savedScore = scoreService.addScore(
                    user,
                    request.stablefordScore(),
                    request.scoreDate()
            );

            return ResponseEntity.ok(
                    new ScoreResponse(
                            savedScore.getId(),
                            savedScore.getStablefordScore(),
                            savedScore.getScoreDate()
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateScore(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestBody ScoreRequest request) {

        try {
            User user = authService.findByEmail(email);

            Score updatedScore = scoreService.updateScore(
                    user,
                    id,
                    request.stablefordScore(),
                    request.scoreDate()
            );

            return ResponseEntity.ok(
                    new ScoreResponse(
                            updatedScore.getId(),
                            updatedScore.getStablefordScore(),
                            updatedScore.getScoreDate()
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScore(
            @PathVariable Long id,
            @RequestParam String email) {

        try {
            User user = authService.findByEmail(email);

            scoreService.deleteScore(user, id);

            return ResponseEntity.ok(
                    "Score deleted successfully."
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    public record ScoreRequest(
            int stablefordScore,
            LocalDate scoreDate
    ) {
    }

    public record ScoreResponse(
            Long id,
            int stablefordScore,
            LocalDate scoreDate
    ) {
    }
}