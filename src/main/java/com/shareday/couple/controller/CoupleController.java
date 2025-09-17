package com.shareday.couple.controller;

import com.shareday.couple.dto.CoupleRequest;
import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.service.CoupleService;
import com.shareday.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "커플 컨트롤러", description = "커플 관리 API")
@RequestMapping("/api/couples")
public class CoupleController {

    private final CoupleService coupleService;

    public CoupleController(CoupleService coupleService) {
        this.coupleService = coupleService;
    }

    //TODO:: 각 API가 무슨기능을 하는지 깃 이슈에 정리하기
    @PostMapping
    @Operation(summary = "커플 생성", description = "새로운 커플을 등록합니다.")
    public ResponseEntity<ApiResponse<CoupleResponse>> create(@RequestBody CoupleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(coupleService.create(request)));
    }

    @PatchMapping("/{coupleId}")
    @Operation(summary = "커플 수정", description = "커플 ID에 해당하는 커플 정보를 수정합니다. (교제 시작일)")
    public ResponseEntity<ApiResponse<CoupleResponse>> update(
            @PathVariable Long coupleId,
            @RequestBody CoupleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(coupleService.update(coupleId, request)));
    }

    @DeleteMapping("/{coupleId}")
    @Operation(summary = "커플 해제", description = "커플 ID에 해당하는 커플을 해제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long coupleId) {
        coupleService.delete(coupleId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/invite")
    @Operation(summary = "초대 코드 생성", description = "커플 초대 코드를 생성합니다.")
    public ResponseEntity<ApiResponse<InviteResponse>> invite() {
        return ResponseEntity.ok(ApiResponse.ok(coupleService.generateInviteCode()));
    }

    @PostMapping("/accept")
    @Operation(summary = "초대 코드 수락", description = "초대 코드를 이용해 커플을 수락합니다.")
    public ResponseEntity<ApiResponse<CoupleResponse>> accept(@RequestBody InviteAcceptRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(coupleService.acceptInvite(request)));
    }
}
