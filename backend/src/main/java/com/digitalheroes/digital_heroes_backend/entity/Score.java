package com.digitalheroes.digital_heroes_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "scores",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_score_date",
                        columnNames = {"user_id", "score_date"}
                )
        }
)
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stableford_score", nullable = false)
    private int stablefordScore;

    @Column(name = "score_date", nullable = false)
    private LocalDate scoreDate;

    public Score() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStablefordScore() {
        return stablefordScore;
    }

    public void setStablefordScore(int stablefordScore) {
        this.stablefordScore = stablefordScore;
    }

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }
}