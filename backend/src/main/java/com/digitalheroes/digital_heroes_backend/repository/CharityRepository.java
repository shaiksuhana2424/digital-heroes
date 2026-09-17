package com.digitalheroes.digital_heroes_backend.repository;

import com.digitalheroes.digital_heroes_backend.entity.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharityRepository extends JpaRepository<Charity, Long> {

    List<Charity> findByActiveTrueOrderByFeaturedDescNameAsc();

    List<Charity> findByFeaturedTrueAndActiveTrueOrderByNameAsc();

    Optional<Charity> findByNameIgnoreCase(String name);
}