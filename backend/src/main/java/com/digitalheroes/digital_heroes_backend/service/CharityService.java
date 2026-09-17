package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.Charity;
import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.repository.CharityRepository;
import com.digitalheroes.digital_heroes_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final UserRepository userRepository;

    public CharityService(
            CharityRepository charityRepository,
            UserRepository userRepository
    ) {
        this.charityRepository = charityRepository;
        this.userRepository = userRepository;
    }

    public List<Charity> getCharities(
            String search,
            Boolean featured
    ) {

        List<Charity> charities =
                charityRepository.findByActiveTrueOrderByFeaturedDescNameAsc();

        if (search != null && !search.isBlank()) {

            String value = search.trim().toLowerCase();

            charities = charities.stream()
                    .filter(charity ->
                            charity.getName()
                                    .toLowerCase()
                                    .contains(value)
                                    ||
                            (charity.getDescription() != null &&
                             charity.getDescription()
                                    .toLowerCase()
                                    .contains(value))
                    )
                    .toList();
        }

        if (Boolean.TRUE.equals(featured)) {

            charities = charities.stream()
                    .filter(Charity::isFeatured)
                    .toList();
        }

        return charities;
    }

    public Charity getById(Long id) {

        return charityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Charity not found.")
                );
    }

    public Charity create(
            String adminEmail,
            String name,
            String description,
            String website,
            String imageUrl,
            boolean featured
    ) {

        requireAdmin(adminEmail);

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Charity name is required.");
        }

        if (charityRepository
                .findByNameIgnoreCase(name.trim())
                .isPresent()) {

            throw new RuntimeException(
                    "A charity with this name already exists."
            );
        }

        Charity charity = new Charity(
                name.trim(),
                description,
                website,
                imageUrl,
                featured
        );

        return charityRepository.save(charity);
    }

    public Charity update(
            String adminEmail,
            Long id,
            String name,
            String description,
            String website,
            String imageUrl,
            boolean featured,
            boolean active
    ) {

        requireAdmin(adminEmail);

        Charity charity = getById(id);

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Charity name is required.");
        }

        charity.setName(name.trim());
        charity.setDescription(description);
        charity.setWebsite(website);
        charity.setImageUrl(imageUrl);
        charity.setFeatured(featured);
        charity.setActive(active);

        return charityRepository.save(charity);
    }

    public Charity deactivate(
            String adminEmail,
            Long id
    ) {

        requireAdmin(adminEmail);

        Charity charity = getById(id);

        charity.setActive(false);

        return charityRepository.save(charity);
    }

    private User requireAdmin(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException(
                    "Admin email is required."
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Admin user not found.")
                );

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException(
                    "Administrator access required."
            );
        }

        return user;
    }
}