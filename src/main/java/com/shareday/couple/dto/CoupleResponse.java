package com.shareday.couple.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CoupleResponse(
        Long coupleId,
        LocalDate startDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
