package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    public String createCheckoutSession(
            String email,
            Subscription.Plan plan,
            int charityContribution) throws StripeException {

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new RuntimeException(
                    "Stripe secret key is not configured."
            );
        }

        long amount;

        if (plan == Subscription.Plan.MONTHLY) {
            amount = 999; // $9.99
        } else {
            amount = 9999; // $99.99
        }

        String planName =
                plan == Subscription.Plan.MONTHLY
                        ? "Digital Heroes Monthly"
                        : "Digital Heroes Yearly";

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(
                                SessionCreateParams.Mode.SUBSCRIPTION
                        )
                        .setSuccessUrl(
                                "http://localhost:5173/payment-success"
                        )
                        .setCancelUrl(
                                "http://localhost:5173/subscription"
                        )
                        .setCustomerEmail(email)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams
                                                        .LineItem
                                                        .PriceData
                                                        .builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(amount)
                                                        .setRecurring(
                                                                SessionCreateParams
                                                                        .LineItem
                                                                        .PriceData
                                                                        .Recurring
                                                                        .builder()
                                                                        .setInterval(
                                                                                plan == Subscription.Plan.MONTHLY
                                                                                        ? SessionCreateParams
                                                                                                .LineItem
                                                                                                .PriceData
                                                                                                .Recurring
                                                                                                .Interval.MONTH
                                                                                        : SessionCreateParams
                                                                                                .LineItem
                                                                                                .PriceData
                                                                                                .Recurring
                                                                                                .Interval.YEAR
                                                                        )
                                                                        .build()
                                                        )
                                                        .setProductData(
                                                                SessionCreateParams
                                                                        .LineItem
                                                                        .PriceData
                                                                        .ProductData
                                                                        .builder()
                                                                        .setName(planName)
                                                                        .setDescription(
                                                                                "Digital Heroes golf, charity and monthly draw subscription"
                                                                        )
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .putMetadata(
                                "email",
                                email
                        )
                        .putMetadata(
                                "plan",
                                plan.name()
                        )
                        .putMetadata(
                                "charityContribution",
                                String.valueOf(charityContribution)
                        )
                        .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}