package com.shareday.event.service;

import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import com.shareday.event.dto.EventRequest;
import com.shareday.event.dto.EventResponse;
import com.shareday.event.dto.EventUpdateRequest;
import com.shareday.event.entity.Event;
import com.shareday.event.repository.EventRepository;
import com.shareday.user.entity.User;
import com.shareday.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    /**
     * 특정 커플의 일정 조회 (기간별)
     */
    public List<EventResponse> getEvents(Long coupleId, LocalDate start, LocalDate end) {
        return eventRepository.findByCouple_CoupleIdAndEventDateBetween(coupleId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 이벤트 생성
     */
    public EventResponse createEvent(Long coupleId, EventRequest request) {
        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(() -> new IllegalArgumentException("커플을 찾을 수 없습니다."));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Event event = Event.builder()
                .couple(couple)
                .user(user)
                .title(request.title())
                .description(request.description())
                .eventDate(request.eventDate())
                .type(request.type())
                .isDday(request.isDday())
                .build();

        return toResponse(eventRepository.save(event));
    }

    /**
     * 이벤트 수정
     */
    public EventResponse updateEvent(Long coupleId, Long eventId, EventUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getCouple().getCoupleId().equals(coupleId)) {
            throw new IllegalArgumentException("해당 커플의 이벤트가 아닙니다.");
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setType(request.type());
        event.setIsDday(request.isDday());

        return toResponse(eventRepository.save(event));
    }

    /**
     * 이벤트 삭제
     */
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    /**
     * Entity → DTO 변환
     */
    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getEventId(),
                e.getCouple().getCoupleId(),
                e.getUser().getUserId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getType(),
                e.getIsDday(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
