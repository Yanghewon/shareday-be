package com.shareday.couple.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CoupleResponse(
        Long coupleId,
        LocalDate startDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt //교제 시작일을 변경했을 때 기록
) {}
