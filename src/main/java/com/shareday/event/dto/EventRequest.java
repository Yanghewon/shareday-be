package com.shareday.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventRequest(

        Long userId,

        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
        String title,

        @Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
        String description,

        @NotNull(message = "이벤트 날짜는 필수입니다.")
        LocalDate eventDate,

        String type,

        @NotNull(message = "D-Day 여부는 필수입니다.")
        Boolean isDday
) {}
