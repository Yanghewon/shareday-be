package com.shareday.couple.controller;

import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.service.CoupleService;
import com.shareday.common.api.ApiResponse;
import com.shareday.user.repository.UserRepository;
import com.shareday.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "커플 컨트롤러", description = "커플 관리 API")
@RequestMapping("/api/couples")
public class CoupleController {

    private final CoupleService coupleService;
    private final UserRepository userRepository;

    public CoupleController(CoupleService coupleService, UserRepository userRepository) {
        this.coupleService = coupleService;
        this.userRepository = userRepository;
    }

    /** ✅ 초대 코드 생성 */
    @PostMapping("/invite")
    @Operation(summary = "초대 코드 생성", description = "현재 로그인한 사용자로 초대 코드를 생성합니다.")
    public ResponseEntity<ApiResponse<InviteResponse>> invite(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        // --- OAuth2User에서 email 가져오기 ---
        String email = extractEmail(principal);
        System.out.println("✅ 로그인 사용자 email = " + email);

        // --- email로 사용자 조회 ---
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        return ResponseEntity.ok(ApiResponse.ok(coupleService.generateInviteCode(user.getId())));
    }

    /** ✅ 초대 코드 수락 */
    @PostMapping("/accept")
    @Operation(summary = "초대 코드 수락", description = "초대 코드를 이용해 커플을 확정합니다.")
    public ResponseEntity<ApiResponse<CoupleResponse>> accept(
            @RequestBody InviteAcceptRequest request,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        String email = extractEmail(principal);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        return ResponseEntity.ok(ApiResponse.ok(coupleService.acceptInvite(request, user.getId())));
    }

    /** ✅ 커플 해제 */
    @DeleteMapping("/{coupleId}")
    @Operation(summary = "커플 해제", description = "커플 ID에 해당하는 커플을 해제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long coupleId) {
        coupleService.delete(coupleId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // --- ✅ OAuth2User에서 email 속성 추출 ---
    private String extractEmail(OAuth2User principal) {
        if (principal == null) {
            throw new IllegalArgumentException("인증된 사용자가 없습니다.");
        }

        // 1️⃣ 일반 구글/네이버 계정 등
        Object email = principal.getAttribute("email");
        if (email != null) {
            return email.toString();
        }

        // 2️⃣ 카카오 계정: kakao_account.email 안에 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) principal.getAttributes().get("kakao_account");
        if (kakaoAccount != null && kakaoAccount.get("email") != null) {
            return kakaoAccount.get("email").toString();
        }

        throw new IllegalArgumentException("OAuth2User에서 이메일을 찾을 수 없습니다: " + principal.getAttributes());
    }
}
