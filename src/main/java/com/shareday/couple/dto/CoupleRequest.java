package com.shareday.couple.dto;

import com.shareday.auth.entity.User;

import java.time.LocalDate;

public record CoupleRequest(
        LocalDate startDate
) {}
