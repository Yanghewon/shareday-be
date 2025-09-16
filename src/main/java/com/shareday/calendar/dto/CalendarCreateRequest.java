package com.shareday.calendar.dto;

import com.shareday.calendar.entity.Calendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CalendarCreateRequest(
        String title,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String createdBy,
        String participants,
        String description,
        Calendar.EventType type,
        boolean isDDay
) {}
