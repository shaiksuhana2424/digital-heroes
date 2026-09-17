package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.entity.Charity;
import com.digitalheroes.digital_heroes_backend.service.CharityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charities")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
        }
)
public class CharityController {

    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    @GetMapping
    public ResponseEntity<?> getCharities(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean featured
    ) {

        try {

            List<Charity> charities =
                    charityService.getCharities(
                            search,
                            featured
                    );

            return ResponseEntity.ok(charities);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCharity(
            @PathVariable Long id
    ) {

        try {
            return ResponseEntity.ok(
                    charityService.getById(id)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCharity(
            @RequestParam String adminEmail,
            @RequestBody CharityRequest request
    ) {

        try {

            Charity charity =
                    charityService.create(
                            adminEmail,
                            request.name(),
                            request.description(),
                            request.website(),
                            request.imageUrl(),
                            request.featured()
                    );

            return ResponseEntity.ok(charity);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCharity(
            @PathVariable Long id,
            @RequestParam String adminEmail,
            @RequestBody CharityRequest request
    ) {

        try {

            Charity charity =
                    charityService.update(
                            adminEmail,
                            id,
                            request.name(),
                            request.description(),
                            request.website(),
                            request.imageUrl(),
                            request.featured(),
                            request.active()
                    );

            return ResponseEntity.ok(charity);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharity(
            @PathVariable Long id,
            @RequestParam String adminEmail
    ) {

        try {

            return ResponseEntity.ok(
                    charityService.deactivate(
                            adminEmail,
                            id
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    public record CharityRequest(
            String name,
            String description,
            String website,
            String imageUrl,
            boolean featured,
            boolean active
    ) {
    }
}