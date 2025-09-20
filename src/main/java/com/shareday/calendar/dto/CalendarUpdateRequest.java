package com.shareday.calendar.dto;

import com.shareday.calendar.entity.Calendar;
import com.shareday.calendar.enums.ParticipantType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CalendarUpdateRequest(
        String title,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description,
        ParticipantType participantType
) {}
