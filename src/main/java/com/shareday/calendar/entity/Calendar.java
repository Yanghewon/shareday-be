package com.shareday.calendar.entity;

//import com.shareday.user.entity.User;
import com.shareday.couple.entity.Couple;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_event")
public class Calendar {

    public enum EventType {
        PERSONAL, SHARED, PARTNER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGINT AUTO_INCREMENT
    @Column(name = "event_id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

    // User 만들면 이 코드 사용
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @Column(name = "user_id")
    private Long userId;


    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(length = 255)
    private String participants;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EventType type;

    @Column(name = "is_dday", nullable = false)
    private boolean isDDay = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Calendar() {}

    public Calendar(String title, LocalDate eventDate, LocalDateTime startTime, LocalDateTime endTime,
                    String createdBy, String participants, String description,
                    EventType type, boolean isDDay) {
        this.title = title;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.participants = participants;
        this.description = description;
        this.type = type;
        this.isDDay = isDDay;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getter
    public Long getId() { return id; }
    public Couple getCouple() { return couple; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getCreatedBy() { return createdBy; }
    public String getParticipants() { return participants; }
    public EventType getType() { return type; }
    public boolean isDDay() { return isDDay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Update
    public void update(String title, LocalDate eventDate, LocalDateTime startTime,
                       LocalDateTime endTime, String participants,
                       String description, EventType type, boolean isDDay) {
        this.title = title;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.description = description;
        this.type = type;
        this.isDDay = isDDay;
    }
}