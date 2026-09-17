package com.digitalheroes.digital_heroes_backend.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @PostConstruct
    public void initializeStripe() {

        if (secretKey == null || secretKey.isBlank()) {
            return;
        }

        Stripe.apiKey = secretKey;
    }
}