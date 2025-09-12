package com.shareday.calendar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "calendar_event")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String createdBy; // 이벤트 생성자

    @Column
    private String participants; // N명 참여자 (쉼표로 구분 가능)

    @Column(length = 500)
    private String description;

    // 기본 생성자
    protected CalendarEvent() {}

    public CalendarEvent(String title, LocalDateTime startTime, LocalDateTime endTime,
                         String createdBy, String participants, String description) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.participants = participants;
        this.description = description;
    }

    // Getter / Setter
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getCreatedBy() { return createdBy; }
    public String getParticipants() { return participants; }
    public String getDescription() { return description; }

    public void update(String title, LocalDateTime startTime, LocalDateTime endTime,
                       String participants, String description) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.description = description;
    }
}
