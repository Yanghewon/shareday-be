package com.shareday.event.service;

import com.shareday.event.dto.*;
import com.shareday.event.entity.Event;
import com.shareday.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getEvents(Long coupleId) {
        return eventRepository.findByCoupleId(coupleId).stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .coupleId(request.coupleId())
                .userId(request.userId())
                .title(request.title())
                .description(request.description())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .participantType(request.participantType())
                .isDday(request.isDday() != null ? request.isDday() : false) // ✅ 기본값 처리
                .build();

        return toResponse(eventRepository.save(event));
    }

    public EventResponse updateEvent(Long eventId, EventUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setParticipantType(request.participantType());

        return toResponse(eventRepository.save(event));
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getEventId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getParticipantType(),
                e.getIsDday()
        );
    }
}
