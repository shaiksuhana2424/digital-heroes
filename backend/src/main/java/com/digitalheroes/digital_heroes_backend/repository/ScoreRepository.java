package com.digitalheroes.digital_heroes_backend.repository;

import com.digitalheroes.digital_heroes_backend.entity.Score;
import com.digitalheroes.digital_heroes_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findTop5ByUserOrderByScoreDateDesc(User user);

    Optional<Score> findByUserAndScoreDate(User user, java.time.LocalDate scoreDate);

    List<Score> findByUserOrderByScoreDateDesc(User user);
}