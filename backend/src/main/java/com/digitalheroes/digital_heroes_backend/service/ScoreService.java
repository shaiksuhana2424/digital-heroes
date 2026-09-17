package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Score;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public List<Score> getLatestScores(User user) {
        return scoreRepository.findTop5ByUserOrderByScoreDateDesc(user);
    }

    public Score addScore(
            User user,
            int stablefordScore,
            LocalDate scoreDate) {

        validateScore(stablefordScore, scoreDate);

        if (scoreRepository
                .findByUserAndScoreDate(user, scoreDate)
                .isPresent()) {

            throw new RuntimeException(
                    "A score already exists for this date."
            );
        }

        Score score = new Score();
        score.setUser(user);
        score.setStablefordScore(stablefordScore);
        score.setScoreDate(scoreDate);

        Score savedScore = scoreRepository.save(score);

        removeScoresBeyondLatestFive(user);

        return savedScore;
    }

    public Score updateScore(
            User user,
            Long scoreId,
            int stablefordScore,
            LocalDate scoreDate) {

        validateScore(stablefordScore, scoreDate);

        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() ->
                        new RuntimeException("Score not found."));

        if (!score.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to edit this score."
            );
        }

        scoreRepository
                .findByUserAndScoreDate(user, scoreDate)
                .ifPresent(existingScore -> {
                    if (!existingScore.getId().equals(scoreId)) {
                        throw new RuntimeException(
                                "A score already exists for this date."
                        );
                    }
                });

        score.setStablefordScore(stablefordScore);
        score.setScoreDate(scoreDate);

        return scoreRepository.save(score);
    }

    public void deleteScore(User user, Long scoreId) {

        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() ->
                        new RuntimeException("Score not found."));

        if (!score.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to delete this score."
            );
        }

        scoreRepository.delete(score);
    }

    private void validateScore(
            int stablefordScore,
            LocalDate scoreDate) {

        if (stablefordScore < 1 || stablefordScore > 45) {
            throw new RuntimeException(
                    "Stableford score must be between 1 and 45."
            );
        }

        if (scoreDate == null) {
            throw new RuntimeException(
                    "Score date is required."
            );
        }
    }

    private void removeScoresBeyondLatestFive(User user) {

        List<Score> allScores =
                scoreRepository.findByUserOrderByScoreDateDesc(user);

        if (allScores.size() > 5) {

            List<Score> scoresToDelete =
                    allScores.subList(5, allScores.size());

            scoreRepository.deleteAll(scoresToDelete);
        }
    }
}