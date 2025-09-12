package com.shareday.calendar.controller;

import com.shareday.calendar.dto.*;
import com.shareday.calendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendars")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    public ResponseEntity<CalendarResponse> create(@RequestBody CalendarCreateRequest request) {
        return ResponseEntity.ok(calendarService.createEvent(request));
    }

    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getAll() {
        return ResponseEntity.ok(calendarService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(calendarService.getEvent(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarResponse> update(
            @PathVariable UUID id,
            @RequestBody CalendarUpdateRequest request) {
        return ResponseEntity.ok(calendarService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
