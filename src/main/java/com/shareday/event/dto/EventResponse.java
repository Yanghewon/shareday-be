package com.shareday.event.dto;

import com.shareday.event.enums.ParticipantType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        Long coupleId,
        Long userId,
        String title,
        String description,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String createdBy,
        String participants,
        ParticipantType type,
        Boolean isDday,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}