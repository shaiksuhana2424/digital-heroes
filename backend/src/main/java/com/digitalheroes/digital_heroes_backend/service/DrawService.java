package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Draw;
import com.digitalheroes.digital_heroes_backend.entity.Score;
import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.entity.Winner;
import com.digitalheroes.digital_heroes_backend.repository.DrawRepository;
import com.digitalheroes.digital_heroes_backend.repository.ScoreRepository;
import com.digitalheroes.digital_heroes_backend.repository.SubscriptionRepository;
import com.digitalheroes.digital_heroes_backend.repository.UserRepository;
import com.digitalheroes.digital_heroes_backend.repository.WinnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DrawService {

    private final DrawRepository drawRepository;
    private final WinnerRepository winnerRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DrawService(
            DrawRepository drawRepository,
            WinnerRepository winnerRepository,
            UserRepository userRepository,
            ScoreRepository scoreRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.drawRepository = drawRepository;
        this.winnerRepository = winnerRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Draw> getHistory() {
        return drawRepository.findAllByOrderByDrawMonthDesc();
    }

    public Draw getCurrent() {

        return drawRepository
                .findTopByPublishedTrueOrderByDrawMonthDesc()
                .orElse(null);
    }

    public Draw getById(Long id) {

        return drawRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Draw not found.")
                );
    }

    public Draw simulate(
            String adminEmail,
            YearMonth month
    ) {

        requireAdmin(adminEmail);

        if (month == null) {
            month = YearMonth.now();
        }

        LocalDate drawDate = month.atDay(1);

        Optional<Draw> existing =
                drawRepository.findByDrawMonth(drawDate);

        Draw draw;

        if (existing.isPresent()) {

            draw = existing.get();

            if (draw.isPublished()) {
                throw new RuntimeException(
                        "This month's draw has already been published."
                );
            }

        } else {

            draw = new Draw();
            draw.setDrawMonth(drawDate);
        }

        List<Integer> numbers =
                generateWinningNumbers();

        draw.setWinningNumber1(numbers.get(0));
        draw.setWinningNumber2(numbers.get(1));
        draw.setWinningNumber3(numbers.get(2));
        draw.setWinningNumber4(numbers.get(3));
        draw.setWinningNumber5(numbers.get(4));

        double pool = calculatePrizePool();

        draw.setPrizePool(pool);

        double jackpot = pool * 0.40;

        Draw previous =
                findPreviousPublishedDraw(drawDate);

        if (previous != null) {

            long previousFiveWinners =
                    winnerRepository.countByDrawAndMatchTier(
                            previous,
                            Winner.MatchTier.FIVE_MATCH
                    );

            if (previousFiveWinners == 0) {
                jackpot += previous.getJackpotAmount();
            }
        }

        draw.setJackpotAmount(jackpot);
        draw.setFourMatchPrize(pool * 0.35);
        draw.setThreeMatchPrize(pool * 0.25);

        draw.setSimulation(true);
        draw.setPublished(false);

        return drawRepository.save(draw);
    }

    @Transactional
    public Draw publish(
            String adminEmail,
            Long drawId
    ) {

        requireAdmin(adminEmail);

        Draw draw = getById(drawId);

        if (draw.isPublished()) {
            throw new RuntimeException(
                    "This draw has already been published."
            );
        }

        /*
         * Refresh prize pool at publication time.
         */
        double pool = calculatePrizePool();

        draw.setPrizePool(pool);

        double jackpot = pool * 0.40;

        Draw previous =
                findPreviousPublishedDraw(
                        draw.getDrawMonth()
                );

        if (previous != null) {

            long previousFiveWinners =
                    winnerRepository.countByDrawAndMatchTier(
                            previous,
                            Winner.MatchTier.FIVE_MATCH
                    );

            if (previousFiveWinners == 0) {
                jackpot += previous.getJackpotAmount();
            }
        }

        draw.setJackpotAmount(jackpot);
        draw.setFourMatchPrize(pool * 0.35);
        draw.setThreeMatchPrize(pool * 0.25);

        /*
         * Remove any old winner records for this draw.
         */
        winnerRepository.deleteByDraw(draw);

        List<User> activeUsers =
                userRepository.findBySubscribedTrue();

        List<UserMatch> fiveMatches = new ArrayList<>();
        List<UserMatch> fourMatches = new ArrayList<>();
        List<UserMatch> threeMatches = new ArrayList<>();

        Set<Integer> winningNumbers =
                Set.of(
                        draw.getWinningNumber1(),
                        draw.getWinningNumber2(),
                        draw.getWinningNumber3(),
                        draw.getWinningNumber4(),
                        draw.getWinningNumber5()
                );

        for (User user : activeUsers) {

            if (user.getSubscriptionStatus() != null &&
                    !"ACTIVE".equalsIgnoreCase(
                            user.getSubscriptionStatus()
                    )) {
                continue;
            }

            List<Score> scores =
                    scoreRepository
                            .findTop5ByUserOrderByScoreDateDesc(
                                    user
                            );

            Set<Integer> userScores = new HashSet<>();

            for (Score score : scores) {
                userScores.add(
                        score.getStablefordScore()
                );
            }

            int matched = 0;

            for (Integer number : winningNumbers) {

                if (userScores.contains(number)) {
                    matched++;
                }
            }

            UserMatch match =
                    new UserMatch(user, matched);

            if (matched == 5) {
                fiveMatches.add(match);
            } else if (matched == 4) {
                fourMatches.add(match);
            } else if (matched == 3) {
                threeMatches.add(match);
            }
        }

        /*
         * Five-number jackpot.
         */
        if (!fiveMatches.isEmpty()) {

            double amount =
                    jackpot / fiveMatches.size();

            for (UserMatch match : fiveMatches) {

                winnerRepository.save(
                        new Winner(
                                draw,
                                match.user(),
                                Winner.MatchTier.FIVE_MATCH,
                                5,
                                amount
                        )
                );
            }
        }

        /*
         * Four-number prize.
         */
        if (!fourMatches.isEmpty()) {

            double amount =
                    draw.getFourMatchPrize()
                            / fourMatches.size();

            for (UserMatch match : fourMatches) {

                winnerRepository.save(
                        new Winner(
                                draw,
                                match.user(),
                                Winner.MatchTier.FOUR_MATCH,
                                4,
                                amount
                        )
                );
            }
        }

        /*
         * Three-number prize.
         */
        if (!threeMatches.isEmpty()) {

            double amount =
                    draw.getThreeMatchPrize()
                            / threeMatches.size();

            for (UserMatch match : threeMatches) {

                winnerRepository.save(
                        new Winner(
                                draw,
                                match.user(),
                                Winner.MatchTier.THREE_MATCH,
                                3,
                                amount
                        )
                );
            }
        }

        draw.setPublished(true);
        draw.setSimulation(false);
        draw.setPublishedAt(LocalDateTime.now());

        return drawRepository.save(draw);
    }

    private double calculatePrizePool() {

        List<User> users =
                userRepository.findBySubscribedTrue();

        double total = 0.0;

        for (User user : users) {

            if (user.getSubscriptionStatus() != null &&
                    !"ACTIVE".equalsIgnoreCase(
                            user.getSubscriptionStatus()
                    )) {
                continue;
            }

            Optional<Subscription> subscription =
                    subscriptionRepository.findByUser(user);

            if (subscription.isEmpty()) {
                continue;
            }

            Subscription sub =
                    subscription.get();

            if (sub.getStatus()
                    != Subscription.Status.ACTIVE) {
                continue;
            }

            double amount = sub.getAmount();

            /*
             * Annual subscription is converted
             * to a monthly equivalent for the monthly draw.
             */
            if (sub.getPlan()
                    == Subscription.Plan.YEARLY) {

                amount = amount / 12.0;
            }

            total += amount;
        }

        return Math.round(total * 100.0) / 100.0;
    }

    private List<Integer> generateWinningNumbers() {

        Set<Integer> numbers =
                new LinkedHashSet<>();

        while (numbers.size() < 5) {

            numbers.add(
                    ThreadLocalRandom.current()
                            .nextInt(1, 46)
            );
        }

        return new ArrayList<>(numbers);
    }

    private Draw findPreviousPublishedDraw(
            LocalDate currentMonth
    ) {

        return drawRepository
                .findAllByOrderByDrawMonthDesc()
                .stream()
                .filter(Draw::isPublished)
                .filter(draw ->
                        draw.getDrawMonth()
                                .isBefore(currentMonth))
                .findFirst()
                .orElse(null);
    }

    private User requireAdmin(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin user not found."
                        )
                );

        if (user.getRole() != User.Role.ADMIN) {

            throw new RuntimeException(
                    "Administrator access required."
            );
        }

        return user;
    }

    private record UserMatch(
            User user,
            int matched
    ) {
    }
}