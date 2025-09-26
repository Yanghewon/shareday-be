package com.shareday.event.dto;

import java.time.LocalDate;

public record EventUpdateRequest(
        String title,
        String description,
        LocalDate eventDate,
        String type,
        Boolean isDday
) {}
