package com.shareday.user.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.user.dto.UserResponse;
import com.shareday.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 API", description = "유저 정보 조회 관련 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 사용자 정보 조회")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 🔒 인증 여부 확인
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증되지 않은 사용자입니다."));
        }

        try {
            // ✅ JwtAuthenticationFilter에서 username = userId 로 설정되어 있음
            Long userId = Long.parseLong(auth.getName());
            log.debug("🔍 현재 인증된 사용자 ID: {}", userId);

            UserResponse user = userService.getMe(userId);
            return ResponseEntity.ok(ApiResponse.ok(user));

        } catch (NumberFormatException e) {
            log.error("❌ 잘못된 JWT 토큰 형식: {}", auth.getName());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("잘못된 JWT 토큰 형식입니다."));
        } catch (RuntimeException e) {
            log.error("❌ 사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
