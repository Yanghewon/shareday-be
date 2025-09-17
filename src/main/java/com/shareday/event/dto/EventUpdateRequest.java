package com.shareday.event.dto;

import com.shareday.event.enums.ParticipantType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventUpdateRequest(
        String title,
        String description,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String participants,
        ParticipantType type,
        Boolean isDday
) {}