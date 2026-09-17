package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Draw;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.entity.Winner;
import com.digitalheroes.digital_heroes_backend.repository.DrawRepository;
import com.digitalheroes.digital_heroes_backend.repository.UserRepository;
import com.digitalheroes.digital_heroes_backend.repository.WinnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WinnerService {

    private final WinnerRepository winnerRepository;
    private final UserRepository userRepository;
    private final DrawRepository drawRepository;

    public WinnerService(
            WinnerRepository winnerRepository,
            UserRepository userRepository,
            DrawRepository drawRepository
    ) {
        this.winnerRepository = winnerRepository;
        this.userRepository = userRepository;
        this.drawRepository = drawRepository;
    }

    /*
     * Get all winners belonging to a particular user.
     */
    public List<Winner> getWinnersForUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        return winnerRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    /*
     * Get all winners.
     *
     * This is used by the admin dashboard.
     */
    public List<Winner> getAllWinners() {

        return winnerRepository
                .findAllByOrderByCreatedAtDesc();
    }

    /*
     * Upload screenshot/proof for a winning record.
     */
    public Winner uploadProof(
            Long winnerId,
            String email,
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Please select a proof file."
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException(
                    "Proof file must be smaller than 5 MB."
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() ->
                        new RuntimeException("Winner record not found.")
                );

        /*
         * Make sure the winner belongs to the logged-in user.
         */
        if (!winner.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to upload proof for this winner."
            );
        }

        /*
         * A rejected winner can submit proof again.
         * An approved/paid winner should not be changed.
         */
        if (winner.getPayoutStatus() == Winner.PayoutStatus.PAID) {
            throw new RuntimeException(
                    "Proof cannot be changed after the prize has been paid."
            );
        }

        try {

            winner.setProofData(file.getBytes());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read the uploaded proof file."
            );
        }

        winner.setProofContentType(
                file.getContentType()
        );

        winner.setProofFileName(
                file.getOriginalFilename()
        );

        /*
         * New proof must go through admin verification again.
         */
        winner.setVerificationStatus(
                Winner.VerificationStatus.PENDING
        );

        winner.setPayoutStatus(
                Winner.PayoutStatus.PENDING
        );

        winner.setRejectionReason(null);
        winner.setVerifiedAt(null);

        return winnerRepository.save(winner);
    }

    /*
     * Admin approves a winner's submitted proof.
     */
    public Winner approveWinner(Long winnerId) {

        Winner winner = findWinner(winnerId);

        if (winner.getProofData() == null ||
                winner.getProofData().length == 0) {

            throw new RuntimeException(
                    "Winner has not uploaded proof yet."
            );
        }

        winner.setVerificationStatus(
                Winner.VerificationStatus.APPROVED
        );

        winner.setRejectionReason(null);
        winner.setVerifiedAt(LocalDateTime.now());

        /*
         * Payment remains pending until admin marks it paid.
         */
        winner.setPayoutStatus(
                Winner.PayoutStatus.PENDING
        );

        return winnerRepository.save(winner);
    }

    /*
     * Admin rejects a winner's proof.
     */
    public Winner rejectWinner(
            Long winnerId,
            String reason
    ) {

        Winner winner = findWinner(winnerId);

        winner.setVerificationStatus(
                Winner.VerificationStatus.REJECTED
        );

        if (reason == null || reason.isBlank()) {

            winner.setRejectionReason(
                    "Proof could not be verified."
            );

        } else {

            winner.setRejectionReason(
                    reason.trim()
            );
        }

        winner.setVerifiedAt(
                LocalDateTime.now()
        );

        winner.setPayoutStatus(
                Winner.PayoutStatus.PENDING
        );

        return winnerRepository.save(winner);
    }

    /*
     * Admin marks an approved winner's prize as paid.
     */
    public Winner markAsPaid(Long winnerId) {

        Winner winner = findWinner(winnerId);

        if (winner.getVerificationStatus()
                != Winner.VerificationStatus.APPROVED) {

            throw new RuntimeException(
                    "Winner must be approved before payment."
            );
        }

        if (winner.getPayoutStatus()
                == Winner.PayoutStatus.PAID) {

            throw new RuntimeException(
                    "This winner has already been paid."
            );
        }

        winner.setPayoutStatus(
                Winner.PayoutStatus.PAID
        );

        winner.setPaidAt(
                LocalDateTime.now()
        );

        return winnerRepository.save(winner);
    }

    /*
     * Find a winner by ID.
     */
    public Winner findWinner(Long winnerId) {

        return winnerRepository.findById(winnerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Winner record not found."
                        )
                );
    }

    /*
     * Get winners for a particular draw.
     */
    public List<Winner> getWinnersForDraw(Long drawId) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Draw not found."
                        )
                );

        return winnerRepository
                .findByDrawOrderByMatchedCountDesc(draw);
    }

    /*
     * Get winners waiting for admin verification.
     */
    public List<Winner> getPendingVerification() {

        return winnerRepository
                .findByVerificationStatusOrderByCreatedAtAsc(
                        Winner.VerificationStatus.PENDING
                );
    }
}