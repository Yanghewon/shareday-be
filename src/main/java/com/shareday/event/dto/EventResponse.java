package com.shareday.event.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        Long coupleId,
        Long userId,
        String title,
        String description,
        LocalDate eventDate,
        String type,
        Boolean isDday,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
