package com.shareday.calendar.dto;

import com.shareday.calendar.entity.Calendar;
import com.shareday.calendar.enums.ParticipantType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CalendarResponse(
        Long id,
        String title,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description,
        ParticipantType participantType
) {
    public static CalendarResponse from(Calendar event) {
        return new CalendarResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getDescription(),
                event.getParticipantType()
        );
    }
}
