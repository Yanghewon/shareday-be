package com.shareday.auth.controller;

import com.shareday.auth.authService.AuthService;
import com.shareday.auth.dto.SocialSignUpRequest;
import com.shareday.auth.dto.SocialSignUpResponse;
import com.shareday.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "인증 컨트롤러", description = "인증")
public class AuthController {

    private AuthService authService;

    @PostMapping("/social-signUp")
    @Operation(summary = "회원가입", description = "회원가입")
    public ResponseEntity<ApiResponse<SocialSignUpResponse>> socialSignUp (
            @Valid @RequestBody SocialSignUpRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(authService.socialSignUp(request)));
    }
}
