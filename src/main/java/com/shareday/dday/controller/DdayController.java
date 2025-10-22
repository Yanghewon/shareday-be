package com.shareday.dday.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.dday.dto.DdayRequest;
import com.shareday.dday.dto.DdayResponse;
import com.shareday.dday.service.DdayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "D-Day 컨트롤러", description = "D-Day 관리 API")
@RequestMapping("/api/ddays")
public class DdayController {

    private final DdayService ddayService;

    public DdayController(DdayService ddayService) {
        this.ddayService = ddayService;
    }

    @GetMapping
    @Operation(summary = "D-Day 목록 조회", description = "로그인한 사용자의 전체 D-Day 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<DdayResponse>>> getAll() {
        Long userId = getCurrentUserId();
        log.info("📋 [D-DAY 목록 조회] userId = {}", userId);
        return ResponseEntity.ok(ApiResponse.ok(ddayService.getAll(userId)));
    }

    @GetMapping("/{ddayId}")
    @Operation(summary = "D-Day 단건 조회", description = "특정 D-Day 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<DdayResponse>> getOne(@PathVariable Long ddayId) {
        Long userId = getCurrentUserId();
        log.info("📋 [D-DAY 단건 조회] ddayId = {}, userId = {}", ddayId, userId);
        return ResponseEntity.ok(ApiResponse.ok(ddayService.getOne(ddayId, userId)));
    }

    @PostMapping
    @Operation(summary = "D-Day 생성", description = "새로운 D-Day를 생성합니다. (자동으로 isDday = true)")
    public ResponseEntity<ApiResponse<DdayResponse>> create(@Valid @RequestBody DdayRequest request) {
        Long userId = getCurrentUserId();
        log.info("🆕 [D-DAY 생성] userId = {}, title = {}", userId, request.title());
        return ResponseEntity.ok(ApiResponse.ok(ddayService.create(request, userId)));
    }

    @PatchMapping("/{ddayId}")
    @Operation(summary = "D-Day 수정", description = "특정 D-Day 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<DdayResponse>> update(
            @PathVariable Long ddayId,
            @Valid @RequestBody DdayRequest request) {
        Long userId = getCurrentUserId();
        log.info("✏️ [D-DAY 수정] ddayId = {}, userId = {}", ddayId, userId);
        return ResponseEntity.ok(ApiResponse.ok(ddayService.update(ddayId, request, userId)));
    }

    @DeleteMapping("/{ddayId}")
    @Operation(summary = "D-Day 삭제", description = "특정 D-Day를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long ddayId) {
        Long userId = getCurrentUserId();
        log.info("🗑 [D-DAY 삭제] ddayId = {}, userId = {}", ddayId, userId);
        ddayService.delete(ddayId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * ✅ JWT 인증 정보를 통해 로그인한 사용자의 ID를 가져옵니다.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        try {
            // JwtAuthenticationFilter에서 subject에 userId를 넣은 경우
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("❌ JWT에서 userId 파싱 실패: {}", authentication.getName());
            throw new RuntimeException("유효하지 않은 사용자 인증 정보입니다.");
        }
    }
}
