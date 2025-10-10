package com.shareday.couple.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "couple_invites")
@Getter @Setter
@NoArgsConstructor
public class CoupleInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inviterUserId;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(nullable = false, length = 16)
    private String status = "PENDING"; // PENDING/USED/EXPIRED

    private LocalDateTime expireAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isUsable() {
        return "PENDING".equals(status) &&
                (expireAt == null || expireAt.isAfter(LocalDateTime.now()));
    }

    public void markUsed() {
        this.status = "USED";
    }
}
