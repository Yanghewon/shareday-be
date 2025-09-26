package com.shareday.user.entity;

import com.shareday.couple.entity.Couple;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // ERD에서는 User지만 DB 예약어 문제 방지 위해 users로 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String provider; // 예: GOOGLE, KAKAO 등

    @Column(nullable = false, unique = true, length = 100)
    private String providerId; // OAuth provider가 주는 고유 ID

    @Column(nullable = false, length = 50)
    private String nickname;

    private LocalDate birthDate;

    @Column(length = 1)
    private String gender; // M / F 같은 값

    @Column(unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple; // FK: User → Couple

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
