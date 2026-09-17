package com.digitalheroes.digital_heroes_backend.repository;

import com.digitalheroes.digital_heroes_backend.entity.Draw;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {

    List<Winner> findByUserOrderByCreatedAtDesc(User user);

    List<Winner> findAllByOrderByCreatedAtDesc();

    List<Winner> findByDrawOrderByMatchedCountDesc(Draw draw);

    List<Winner> findByVerificationStatusOrderByCreatedAtAsc(
            Winner.VerificationStatus status
    );

    long countByDrawAndMatchTier(
            Draw draw,
            Winner.MatchTier matchTier
    );

    long countByVerificationStatus(
            Winner.VerificationStatus status
    );

    long countByPayoutStatus(
            Winner.PayoutStatus status
    );

    void deleteByDraw(Draw draw);
}