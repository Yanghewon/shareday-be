package com.shareday.user.dto;

import com.shareday.auth.enums.UserType;

public record TestDto(
        Long id,
        String name,
        String email,
        UserType userType
) { }
