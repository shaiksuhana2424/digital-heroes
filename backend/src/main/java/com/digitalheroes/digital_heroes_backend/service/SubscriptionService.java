package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AuthService authService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            AuthService authService) {

        this.subscriptionRepository = subscriptionRepository;
        this.authService = authService;
    }

    public Subscription createSubscription(
            String email,
            Subscription.Plan plan,
            int charityContribution) {

        if (plan == null) {
            throw new RuntimeException(
                    "Subscription plan is required."
            );
        }

        if (charityContribution < 10 ||
                charityContribution > 100) {

            throw new RuntimeException(
                    "Charity contribution must be between 10% and 100%."
            );
        }

        User user = authService.findByEmail(email);

        Subscription subscription =
                subscriptionRepository.findByUser(user)
                        .orElseGet(Subscription::new);

        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStatus(Subscription.Status.ACTIVE);
        subscription.setCharityContribution(
                charityContribution
        );

        /*
         * These are application plan amounts for now.
         * Stripe payment integration will be connected later.
         */
        if (plan == Subscription.Plan.MONTHLY) {
            subscription.setAmount(9.99);
            subscription.setNextBillingDate(
                    LocalDateTime.now().plusMonths(1)
            );
        } else {
            subscription.setAmount(99.99);
            subscription.setNextBillingDate(
                    LocalDateTime.now().plusYears(1)
            );
        }

        if (subscription.getStartDate() == null) {
            subscription.setStartDate(LocalDateTime.now());
        }

        Subscription savedSubscription =
                subscriptionRepository.save(subscription);

        user.setSubscribed(true);
        user.setSubscriptionPlan(plan.name());
        user.setSubscriptionStatus(
                Subscription.Status.ACTIVE.name()
        );
        user.setCharityContribution(
                charityContribution
        );

        authService.updateCharityPreferences(
                email,
                user.getSelectedCharity() == null
                        ? "Golf For Good Foundation"
                        : user.getSelectedCharity(),
                charityContribution
        );

        return savedSubscription;
    }

    public Subscription getSubscription(String email) {

        User user = authService.findByEmail(email);

        return subscriptionRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Subscription not found."
                        ));
    }

    public Subscription cancelSubscription(String email) {

        User user = authService.findByEmail(email);

        Subscription subscription =
                subscriptionRepository.findByUser(user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Subscription not found."
                                ));

        subscription.setStatus(
                Subscription.Status.CANCELLED
        );

        subscription.setCancelledAt(
                LocalDateTime.now()
        );

        user.setSubscribed(false);
        user.setSubscriptionStatus(
                Subscription.Status.CANCELLED.name()
        );

        return subscriptionRepository.save(subscription);
    }
}