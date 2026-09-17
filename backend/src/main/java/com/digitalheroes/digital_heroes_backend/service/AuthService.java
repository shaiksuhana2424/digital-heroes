package com.digitalheroes.digital_heroes_backend.service;

import com.digitalheroes.digital_heroes_backend.entity.User;
import com.digitalheroes.digital_heroes_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setRole(User.Role.USER);
        user.setSubscribed(false);

        if (user.getCharityContribution() < 10) {
            user.setCharityContribution(10);
        }

        return userRepository.save(user);
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public User updateCharityPreferences(
            String email,
            String selectedCharity,
            int charityContribution) {

        if (selectedCharity == null ||
                selectedCharity.isBlank()) {

            throw new RuntimeException(
                    "Charity selection is required."
            );
        }

        if (charityContribution < 10 ||
                charityContribution > 100) {

            throw new RuntimeException(
                    "Charity contribution must be at least 10%."
            );
        }

        User user = findByEmail(email);

        user.setSelectedCharity(selectedCharity);
        user.setCharityContribution(charityContribution);

        return userRepository.save(user);
    }
}