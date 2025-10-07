package com.shareday.user.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.user.dto.UserResponse;
import com.shareday.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 컨트롤러", description = "현재 로그인한 사용자 정보 조회 API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 사용자 조회", description = "JWT 인증을 기반으로 로그인된 사용자 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getCurrentUser()));
    }
}
