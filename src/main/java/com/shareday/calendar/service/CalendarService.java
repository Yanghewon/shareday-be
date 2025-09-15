package com.shareday.calendar.service;

import com.shareday.calendar.dto.*;
import com.shareday.calendar.entity.CalendarEvent;
import com.shareday.calendar.repository.CalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public CalendarService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Transactional
    public CalendarResponse createEvent(CalendarCreateRequest request) {
        CalendarEvent event = new CalendarEvent(
                request.title(),
                request.startTime(),
                request.endTime(),
                request.createdBy(),
                request.participants(),
                request.description()
        );
        CalendarEvent saved = calendarRepository.save(event);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CalendarResponse> getAllEvents() {
        return calendarRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalendarResponse getEvent(UUID id) {
        CalendarEvent event = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return toResponse(event);
    }

    @Transactional
    public CalendarResponse updateEvent(UUID id, CalendarUpdateRequest request) {
        CalendarEvent event = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.update(
                request.title(),
                request.startTime(),
                request.endTime(),
                request.participants(),
                request.description()
        );
        return toResponse(event);
    }

    @Transactional
    public void deleteEvent(UUID id) {
        calendarRepository.deleteById(id);
    }

    private CalendarResponse toResponse(CalendarEvent event) {
        return new CalendarResponse(
                event.getId(),
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCreatedBy(),
                event.getParticipants(),
                event.getDescription()
        );
    }
}
