package com.shareday.event.dto;

import com.shareday.event.enums.ParticipantType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventUpdateRequest(
        Long coupleId,  // 수정된 필드
        Long userId,    // 수정된 필드
        String title,
        String description,
        LocalDate eventDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ParticipantType participantType,
        Boolean isDday  // 기본값 false 처리
) {
    public EventUpdateRequest {
        // isDday가 null이면 false로 기본값 설정
        if (isDday == null) {
            isDday = false;
        }
    }
}
