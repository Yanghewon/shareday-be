package com.shareday.dday.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DdayRequest(
        @NotNull(message = "제목은 필수입니다.")
        @Size(max = 50)
        String title,

        @NotNull(message = "날짜는 필수입니다.")
        LocalDate targetDate,

        @Size(max = 200)
        String memo
) {}
