
package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.digitalheroes.digital_heroes_backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<?> createSubscription(
            @RequestParam String email,
            @RequestBody SubscriptionRequest request) {

        try {
            Subscription subscription =
                    subscriptionService.createSubscription(
                            email,
                            request.plan(),
                            request.charityContribution()
                    );

            return ResponseEntity.ok(
                    new SubscriptionResponse(subscription)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }


    @PostMapping("/mock-payment")
    public ResponseEntity<?> mockPayment(
            @RequestParam String email,
            @RequestBody SubscriptionRequest request) {

        try {

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Email is required.");
            }

            if (request.plan() == null) {
                return ResponseEntity.badRequest()
                        .body("Subscription plan is required.");
            }

            if (request.charityContribution() < 10 ||
                    request.charityContribution() > 100) {

                return ResponseEntity.badRequest()
                        .body(
                                "Charity contribution must be between 10% and 100%."
                        );
            }

            /*
             * Simulate successful payment.
             * The existing service creates the subscription
             * and stores it as ACTIVE.
             */
            Subscription subscription =
                    subscriptionService.createSubscription(
                            email,
                            request.plan(),
                            request.charityContribution()
                    );

            return ResponseEntity.ok(
                    new MockPaymentResponse(
                            "SUCCESS",
                            "Mock payment completed successfully.",
                            new SubscriptionResponse(subscription)
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSubscription(
            @RequestParam String email) {

        try {
            Subscription subscription =
                    subscriptionService.getSubscription(email);

            return ResponseEntity.ok(
                    new SubscriptionResponse(subscription)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<?> cancelSubscription(
            @RequestParam String email) {

        try {
            Subscription subscription =
                    subscriptionService.cancelSubscription(email);

            return ResponseEntity.ok(
                    new SubscriptionResponse(subscription)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    public record SubscriptionRequest(
            Subscription.Plan plan,
            int charityContribution
    ) {}

    public record MockPaymentResponse(
            String paymentStatus,
            String message,
            SubscriptionResponse subscription
    ) {}

    public record SubscriptionResponse(
            Long id,
            String plan,
            String status,
            double amount,
            int charityContribution,
            String startDate,
            String nextBillingDate,
            String cancelledAt
    ) {

        public SubscriptionResponse(
                Subscription subscription) {

            this(
                    subscription.getId(),
                    subscription.getPlan().name(),
                    subscription.getStatus().name(),
                    subscription.getAmount(),
                    subscription.getCharityContribution(),
                    subscription.getStartDate() == null
                            ? null
                            : subscription.getStartDate().toString(),
                    subscription.getNextBillingDate() == null
                            ? null
                            : subscription.getNextBillingDate().toString(),
                    subscription.getCancelledAt() == null
                            ? null
                            : subscription.getCancelledAt().toString()
            );
        }
    }
}
