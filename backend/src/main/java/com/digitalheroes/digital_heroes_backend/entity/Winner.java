package com.digitalheroes.digital_heroes_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "winners",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_winner_draw_user_tier",
                        columnNames = {"draw_id", "user_id", "match_tier"}
                )
        }
)
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_tier", nullable = false)
    private MatchTier matchTier;

    @Column(nullable = false)
    private int matchedCount;

    @Column(nullable = false)
    private double prizeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "proof_data")
    private byte[] proofData;

    @Column(name = "proof_content_type")
    private String proofContentType;

    @Column(name = "proof_file_name")
    private String proofFileName;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Winner() {
    }

    public Winner(
            Draw draw,
            User user,
            MatchTier matchTier,
            int matchedCount,
            double prizeAmount
    ) {
        this.draw = draw;
        this.user = user;
        this.matchTier = matchTier;
        this.matchedCount = matchedCount;
        this.prizeAmount = prizeAmount;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum MatchTier {
        FIVE_MATCH,
        FOUR_MATCH,
        THREE_MATCH
    }

    public enum VerificationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum PayoutStatus {
        PENDING,
        PAID
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Draw getDraw() {
        return draw;
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MatchTier getMatchTier() {
        return matchTier;
    }

    public void setMatchTier(MatchTier matchTier) {
        this.matchTier = matchTier;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public double getPrizeAmount() {
        return prizeAmount;
    }

    public void setPrizeAmount(double prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public PayoutStatus getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(PayoutStatus payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public byte[] getProofData() {
        return proofData;
    }

    public void setProofData(byte[] proofData) {
        this.proofData = proofData;
    }

    public String getProofContentType() {
        return proofContentType;
    }

    public void setProofContentType(String proofContentType) {
        this.proofContentType = proofContentType;
    }

    public String getProofFileName() {
        return proofFileName;
    }

    public void setProofFileName(String proofFileName) {
        this.proofFileName = proofFileName;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}