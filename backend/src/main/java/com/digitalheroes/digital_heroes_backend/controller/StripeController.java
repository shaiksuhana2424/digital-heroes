package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.Subscription;
import com.digitalheroes.digital_heroes_backend.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(
            @RequestBody CheckoutRequest request) {

        try {

            if (request.email() == null ||
                    request.email().isBlank()) {

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

            String checkoutUrl =
                    stripeService.createCheckoutSession(
                            request.email(),
                            request.plan(),
                            request.charityContribution()
                    );

            return ResponseEntity.ok(
                    new CheckoutResponse(checkoutUrl)
            );

        } catch (StripeException e) {

            return ResponseEntity.badRequest()
                    .body(
                            "Stripe error: " + e.getMessage()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    public record CheckoutRequest(
            String email,
            Subscription.Plan plan,
            int charityContribution
    ) {}

    public record CheckoutResponse(
            String checkoutUrl
    ) {}
}