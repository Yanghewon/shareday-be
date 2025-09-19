package com.shareday.dday.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ddays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "제목은 필수 입력 값입니다.")
    @Size(max = 50, message = "제목은 50자 이내여야 합니다.")
    private String title;

    @NotNull(message = "날짜는 필수 입력 값입니다.")
    private LocalDate targetDate;

    @Column(nullable = false)
    private boolean isDday = true;

    @Size(max = 200, message = "메모는 200자 이내여야 합니다.")
    private String memo;

    // 로그인 사용자 기준
    @NotNull
    private Long userId;
}
