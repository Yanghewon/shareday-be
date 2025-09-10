package com.shareday.user.entity;

import com.shareday.common.enums.UserType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // 테이블명 지정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected User() {
        // JPA 기본 생성자 (protected로 막음)
    }

    public User(String name, UserType userType) {
        this.name = name;
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getter ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserType getUserType() { return userType; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- Setter or 변경 메서드 (필요할 때만) ---
    public void changeName(String newName) {
        this.name = newName;
    }

    public void changeUserType(UserType newType) {
        this.userType = newType;
    }
}

