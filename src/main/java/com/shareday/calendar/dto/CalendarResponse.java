package com.shareday.calendar.dto;

import com.shareday.calendar.entity.Calendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CalendarResponse(
        Long id,
        String title,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String createdBy,
        String participants,
        String description,
        Calendar.EventType type,
        boolean isDDay,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CalendarResponse from(Calendar event) {
        return new CalendarResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCreatedBy(),
                event.getParticipants(),
                event.getDescription(),
                event.getType(),
                event.isDDay(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
