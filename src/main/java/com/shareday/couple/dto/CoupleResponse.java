package com.shareday.couple.dto;

import java.time.LocalDate;

public record CoupleResponse(
        Long coupleId,
        LocalDate startDate,
        String inviteCode
) {}
