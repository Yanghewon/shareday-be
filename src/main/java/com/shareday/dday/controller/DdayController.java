package com.shareday.dday.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.dday.dto.DdayRequest;
import com.shareday.dday.dto.DdayResponse;
import com.shareday.dday.service.DdayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(ApiResponse.ok(ddayService.getAll(getCurrentUserId())));
    }

    @GetMapping("/{ddayId}")
    @Operation(summary = "D-Day 단건 조회", description = "특정 D-Day 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<DdayResponse>> getOne(@PathVariable Long ddayId) {
        return ResponseEntity.ok(ApiResponse.ok(ddayService.getOne(ddayId, getCurrentUserId())));
    }

    @PostMapping
    @Operation(summary = "D-Day 생성", description = "새로운 D-Day를 생성합니다. (자동으로 isDday = true)")
    public ResponseEntity<ApiResponse<DdayResponse>> create(@Valid @RequestBody DdayRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ddayService.create(request, getCurrentUserId())));
    }

    @PatchMapping("/{ddayId}")
    @Operation(summary = "D-Day 수정", description = "특정 D-Day 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<DdayResponse>> update(
            @PathVariable Long ddayId,
            @Valid @RequestBody DdayRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ddayService.update(ddayId, request, getCurrentUserId())));
    }

    @DeleteMapping("/{ddayId}")
    @Operation(summary = "D-Day 삭제", description = "특정 D-Day를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long ddayId) {
        ddayService.delete(ddayId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // TODO: 실제 로그인 연동 시 SecurityContext에서 userId 가져오도록 수정
    private Long getCurrentUserId() {
        return 1L; // 임시 하드코딩
    }
}
