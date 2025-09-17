package com.shareday.calendar.service;

import com.shareday.calendar.dto.*;
import com.shareday.calendar.entity.Calendar;
import com.shareday.calendar.repository.CalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public CalendarService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Transactional
    public CalendarResponse createEvent(CalendarCreateRequest request) {
        Calendar event = new Calendar(
                request.title(),
                request.eventDate(),
                request.startTime(),
                request.endTime(),
                request.createdBy(),
                request.participants(),
                request.description(),
                request.participantType(),
                request.isDDay()
        );
        return CalendarResponse.from(calendarRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<CalendarResponse> getAllEvents() {
        return calendarRepository.findAll().stream()
                .map(CalendarResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalendarResponse getEvent(Long id) {
        Calendar event = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return CalendarResponse.from(event);
    }

    @Transactional
    public CalendarResponse updateEvent(Long id, CalendarUpdateRequest request) {
        Calendar event = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.update(
                request.title(),
                request.eventDate(),
                request.startTime(),
                request.endTime(),
                request.participants(),
                request.description(),
                request.participantType(),
                request.isDDay()
        );
        return CalendarResponse.from(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        calendarRepository.deleteById(id);
    }
}
