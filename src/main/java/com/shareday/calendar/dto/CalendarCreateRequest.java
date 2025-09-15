package com.shareday.calendar.dto;

import java.time.LocalDateTime;

public record CalendarCreateRequest(
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String createdBy,
        String participants,
        String description
) {}
