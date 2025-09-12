package com.shareday.calendar.repository;

import com.shareday.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CalendarRepository extends JpaRepository<CalendarEvent, UUID> {}
