package com.shareday.calendar.dto;

import java.time.LocalDateTime;

public record CalendarUpdateRequest(
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String participants,
        String description
) {}
