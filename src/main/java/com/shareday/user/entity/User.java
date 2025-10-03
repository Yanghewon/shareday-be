<<<<<<<< HEAD:src/main/java/com/shareday/auth/entity/User.java
/*
package com.shareday.auth.entity;
========
package com.shareday.user.entity;
>>>>>>>> 08d6ff6a8fd9b723af63fc9367fc21242abcbb42:src/main/java/com/shareday/user/entity/User.java

import com.shareday.auth.enums.ProviderType;
import com.shareday.couple.entity.Couple;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderType provider;

    @Column(nullable = false, unique = true, length = 100)
    private String providerId;

    @Column(nullable = false, length = 50)
    private String nickname;

    private LocalDate birthDate;

    @Column(length = 1)
    private String gender; // M, F 등 한 글자 코드

    @Column(unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

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
*/
