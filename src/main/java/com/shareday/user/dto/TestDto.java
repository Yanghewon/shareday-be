package com.shareday.user.dto;

import com.shareday.common.enums.UserType;

public record TestDto(
        Long id,
        String name,
        String email,
        UserType userType
) { }
