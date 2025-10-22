package com.shareday.event.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.event.dto.EventRequest;
import com.shareday.event.dto.EventResponse;
import com.shareday.event.dto.EventUpdateRequest;
import com.shareday.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "이벤트 컨트롤러", description = "일정 이벤트 관리 API")
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 이벤트 목록 조회
    @GetMapping
    @Operation(summary = "이벤트 목록 조회", description = "커플 ID로 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(
            @RequestParam Long coupleId
    ) {
        List<EventResponse> events = eventService.getEvents(coupleId);
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    // 이벤트 생성
    @PostMapping
    @Operation(summary = "이벤트 생성", description = "새로운 일정을 등록합니다.")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventRequest request
    ) {
        EventResponse eventResponse = eventService.createEvent(request);
        return ResponseEntity.ok(ApiResponse.ok(eventResponse));
    }

    // 이벤트 수정
    @PatchMapping("/{eventId}")
    @Operation(summary = "이벤트 수정", description = "이벤트 ID로 일정을 수정합니다.")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequest request  // 요청 본문에서 받는 필드
    ) {
        try {
            // EventService에서 받아온 userId, coupleId와 함께 request 전달
            EventResponse updatedEvent = eventService.updateEvent(
                    eventId, request, request.userId(), request.coupleId()
            );
            return ResponseEntity.ok(ApiResponse.ok(updatedEvent));
        } catch (SecurityException e) {
            // 수정 권한이 없는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("수정 권한이 없습니다."));
        } catch (Exception e) {
            // 기타 오류 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("이벤트 수정 중 오류가 발생했습니다."));
        }
    }
    // 이벤트 삭제
    @DeleteMapping("/{eventId}")
    @Operation(summary = "이벤트 삭제", description = "이벤트 ID로 일정을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long eventId
    ) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("이벤트 삭제 중 오류가 발생했습니다."));
        }
    }
}
