package com.shareday.calendar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarResponse(
        UUID id,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String createdBy,
        String participants,
        String description
) {}
