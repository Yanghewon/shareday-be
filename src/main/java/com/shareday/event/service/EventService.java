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

    // ✅ 이벤트 목록 조회
    public List<EventResponse> getEvents(LocalDate start, LocalDate end) {
        return eventRepository.findByEventDateBetween(start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ 이벤트 생성
    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .userId(request.userId())
                .title(request.title())
                .description(request.description())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .participantType(request.participantType())
                .build();

        return toResponse(eventRepository.save(event));
    }

    // ✅ 이벤트 수정
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

    // ✅ 이벤트 삭제
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    // ✅ 엔티티 → DTO 변환
    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getEventId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getParticipantType()
        );
    }
}
