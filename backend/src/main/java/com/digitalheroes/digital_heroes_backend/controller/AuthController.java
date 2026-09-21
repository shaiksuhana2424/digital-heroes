package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "https://digital-heroes-alpha-nine.vercel.app"
})
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthService authService,
            PasswordEncoder passwordEncoder) {

        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {

        try {
            User savedUser = authService.register(user);

            savedUser.setPassword(null);

            return ResponseEntity.ok(savedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {
            User user =
                    authService.findByEmail(request.email());

            if (!passwordEncoder.matches(
                    request.password(),
                    user.getPassword())) {

                return ResponseEntity.status(401)
                        .body("Invalid email or password");
            }

            user.setPassword(null);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }
    }

    @PutMapping("/charity")
    public ResponseEntity<?> updateCharity(
            @RequestParam String email,
            @RequestBody CharityRequest request) {

        try {
            User updatedUser =
                    authService.updateCharityPreferences(
                            email,
                            request.selectedCharity(),
                            request.charityContribution()
                    );

            updatedUser.setPassword(null);

            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {

        return ResponseEntity.ok(
                "Authentication API is working"
        );
    }

    public record LoginRequest(
            String email,
            String password
    ) {
    }

    public record CharityRequest(
            String selectedCharity,
            int charityContribution
    ) {
    }
}