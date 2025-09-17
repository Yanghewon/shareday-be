package com.shareday.event.dto;

import com.shareday.common.annotaion.ValidEnum;
import com.shareday.event.enums.ParticipantType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventRequest(

        Long coupleId,

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

        @NotBlank(message = "생성자 정보는 필수입니다.")
        @Size(max = 100, message = "생성자 정보는 최대 100자까지 입력 가능합니다.")
        String createdBy,

        @Size(max = 255, message = "참여자 목록은 255자를 넘을 수 없습니다.")
        String participants,

        @NotNull(message = "참여자 유형은 필수입니다.")
        @ValidEnum(enumClass = ParticipantType.class, message = "참여자 값이 올바르지 않습니다.")
        ParticipantType participantType,

        @NotNull(message = "D-Day 여부는 필수입니다.")
        Boolean isDday
) {}
