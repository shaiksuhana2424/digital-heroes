package com.digitalheroes.digital_heroes_backend.repository;

import com.digitalheroes.digital_heroes_backend.entity.Draw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {

    Optional<Draw> findByDrawMonth(LocalDate drawMonth);

    List<Draw> findAllByOrderByDrawMonthDesc();

    Optional<Draw> findTopByPublishedTrueOrderByDrawMonthDesc();
}