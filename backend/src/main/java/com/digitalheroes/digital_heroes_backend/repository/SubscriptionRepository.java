package com.digitalheroes.digital_heroes_backend.repository;

import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.digitalheroes.digital_heroes_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser(User user);

    boolean existsByUser(User user);

    Optional<Subscription> findByStripeSubscriptionId(
            String stripeSubscriptionId
    );
}