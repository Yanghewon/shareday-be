package com.shareday.couple.entity;

import com.shareday.event.entity.Event;
import com.shareday.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "couple")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupleId;

    private LocalDate startDate;

    // ✅ 초대 코드 (삭제되지 않고 계속 유지)
    @Column(unique = true, length = 20)
    private String inviteCode;

    @OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
