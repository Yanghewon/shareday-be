package com.shareday.calendar.controller;

import com.shareday.calendar.dto.*;
import com.shareday.calendar.service.CalendarService;
import com.shareday.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "캘린더 컨트롤러", description = "캘린더 일정 관리 API")
@RequestMapping("/api/calendars")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    @Operation(summary = "일정 생성", description = "새로운 일정을 등록합니다.")
    public ResponseEntity<ApiResponse<CalendarResponse>> create(@RequestBody CalendarCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.createEvent(request)));
    }

    @GetMapping
    @Operation(summary = "일정 목록 조회", description = "등록된 모든 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.getAllEvents()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "일정 단건 조회", description = "일정 ID로 특정 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<CalendarResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.getEvent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "일정 수정", description = "일정 ID에 해당하는 일정을 수정합니다.")
    public ResponseEntity<ApiResponse<CalendarResponse>> update(
            @PathVariable Long id,
            @RequestBody CalendarUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.updateEvent(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "일정 삭제", description = "일정 ID에 해당하는 일정을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
