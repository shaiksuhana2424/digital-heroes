package com.digitalheroes.digital_heroes_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "draws",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_draw_month",
                        columnNames = "draw_month"
                )
        }
)
public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "draw_month", nullable = false)
    private LocalDate drawMonth;

    @Column(name = "winning_number_1", nullable = false)
    private int winningNumber1;

    @Column(name = "winning_number_2", nullable = false)
    private int winningNumber2;

    @Column(name = "winning_number_3", nullable = false)
    private int winningNumber3;

    @Column(name = "winning_number_4", nullable = false)
    private int winningNumber4;

    @Column(name = "winning_number_5", nullable = false)
    private int winningNumber5;

    @Column(nullable = false)
    private double prizePool;

    @Column(nullable = false)
    private double jackpotAmount;

    @Column(nullable = false)
    private double fourMatchPrize;

    @Column(nullable = false)
    private double threeMatchPrize;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean simulation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    public Draw() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDrawMonth() {
        return drawMonth;
    }

    public void setDrawMonth(LocalDate drawMonth) {
        this.drawMonth = drawMonth;
    }

    public int getWinningNumber1() {
        return winningNumber1;
    }

    public void setWinningNumber1(int value) {
        this.winningNumber1 = value;
    }

    public int getWinningNumber2() {
        return winningNumber2;
    }

    public void setWinningNumber2(int value) {
        this.winningNumber2 = value;
    }

    public int getWinningNumber3() {
        return winningNumber3;
    }

    public void setWinningNumber3(int value) {
        this.winningNumber3 = value;
    }

    public int getWinningNumber4() {
        return winningNumber4;
    }

    public void setWinningNumber4(int value) {
        this.winningNumber4 = value;
    }

    public int getWinningNumber5() {
        return winningNumber5;
    }

    public void setWinningNumber5(int value) {
        this.winningNumber5 = value;
    }

    public double getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(double prizePool) {
        this.prizePool = prizePool;
    }

    public double getJackpotAmount() {
        return jackpotAmount;
    }

    public void setJackpotAmount(double jackpotAmount) {
        this.jackpotAmount = jackpotAmount;
    }

    public double getFourMatchPrize() {
        return fourMatchPrize;
    }

    public void setFourMatchPrize(double fourMatchPrize) {
        this.fourMatchPrize = fourMatchPrize;
    }

    public double getThreeMatchPrize() {
        return threeMatchPrize;
    }

    public void setThreeMatchPrize(double threeMatchPrize) {
        this.threeMatchPrize = threeMatchPrize;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}