package com.shareday.dday.dto;

import java.time.LocalDate;

public record DdayResponse(
        Long id,
        String title,
        LocalDate targetDate
) {}
