// 기존 EventService 클래스 내용을 EventService.java 파일로 이동
package com.shareday.event.service;

import com.shareday.event.dto.*;
import com.shareday.event.entity.Event;
import com.shareday.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // 이벤트 목록 조회
    public List<EventResponse> getEvents(Long coupleId) {
        return eventRepository.findByCoupleId(coupleId).stream()
                .map(this::toResponse)
                .toList();
    }

    // 새로운 이벤트 생성
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
                .isDday(request.isDday() != null ? request.isDday() : false) // 기본값 처리
                .build();

        return toResponse(eventRepository.save(event));
    }

    // 이벤트 수정
    public EventResponse updateEvent(Long eventId, EventUpdateRequest request, Long userId, Long coupleId) {
        // LocalDateTime으로 파싱
        LocalDateTime startTime = LocalDateTime.parse(request.startTime().toString(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse(request.endTime().toString(), DateTimeFormatter.ISO_DATE_TIME);


        // 수정 권한 확인
        if (!hasEditPermission(eventId, userId, coupleId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 요청된 이벤트 ID로 이벤트 조회
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 유효성 검사
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if (request.eventDate() == null) {
            throw new IllegalArgumentException("이벤트 날짜는 필수입니다.");
        }

        // 수정된 이벤트 필드 업데이트
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setParticipantType(request.participantType());

        return toResponse(eventRepository.save(event));
    }

    // 이벤트 삭제
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    // 수정 권한 확인
    private boolean hasEditPermission(Long eventId, Long userId, Long coupleId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event with ID " + eventId + " not found"));

        // 커플 ID와 사용자 ID 비교
        return event.getCoupleId().equals(coupleId) && event.getUserId().equals(userId);
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
