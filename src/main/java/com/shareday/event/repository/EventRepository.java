package com.shareday.event.repository;

import com.shareday.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDateBetweenAndCoupleId(LocalDate start, LocalDate end, Long coupleId);
}