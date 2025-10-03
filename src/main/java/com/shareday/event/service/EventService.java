package com.shareday.event.service;

import com.shareday.dday.entity.Dday;
import com.shareday.dday.repository.DdayRepository;
import com.shareday.event.dto.EventRequest;
import com.shareday.event.dto.EventResponse;
import com.shareday.event.dto.EventUpdateRequest;
import com.shareday.event.entity.Event;
import com.shareday.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final DdayRepository ddayRepository;

    public List<EventResponse> getEvents(Long coupleId) {
        return eventRepository.findByCoupleId(coupleId).stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse createEvent(EventRequest request) {
        // 1. Event 생성
        Event event = Event.builder()
                .coupleId(request.coupleId())
                .userId(request.userId())
                .title(request.title())
                .description(request.description())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .participantType(request.participantType())
                .isDday(request.isDday() != null ? request.isDday() : false)
                .build();

        Event savedEvent = eventRepository.save(event);

        // 2. isDday = true면 Dday도 생성
        if (Boolean.TRUE.equals(savedEvent.getIsDday())) {
            Dday dday = Dday.builder()
                    .title(savedEvent.getTitle())
                    .targetDate(savedEvent.getEventDate())
                    .userId(savedEvent.getUserId())
                    .build();
            ddayRepository.save(dday);
        }

        return toResponse(savedEvent);
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
        event.setIsDday(request.isDday());

        Event saved = eventRepository.save(event);

        // isDday가 true로 바뀌면 Dday 생성
        if (Boolean.TRUE.equals(saved.getIsDday())) {
            boolean exists = ddayRepository.findAllByUserId(saved.getUserId()).stream()
                    .anyMatch(d -> d.getTitle().equals(saved.getTitle()) && d.getTargetDate().equals(saved.getEventDate()));

            if (!exists) {
                Dday dday = Dday.builder()
                        .title(saved.getTitle())
                        .targetDate(saved.getEventDate())
                        .userId(saved.getUserId())
                        .build();
                ddayRepository.save(dday);
            }
        }

        return toResponse(saved);
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
