package com.shareday.event.dto;

import com.shareday.common.annotaion.ValidEnum;
import com.shareday.event.enums.ParticipantType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventRequest(

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
        String title,

        @Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
        String description,

        @NotNull(message = "이벤트 날짜는 필수입니다.")
        @FutureOrPresent(message = "이벤트 날짜는 오늘 또는 미래여야 합니다.")
        LocalDate eventDate,

        @NotNull(message = "시작 시간은 필수입니다.")
        LocalDateTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        LocalDateTime endTime,

        @NotNull(message = "참여자 유형은 필수입니다.")
        ParticipantType participantType
) {}
