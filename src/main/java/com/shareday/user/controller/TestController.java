package com.shareday.user.controller;

import com.shareday.common.api.ApiResponse;
import com.shareday.user.dto.TestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "테스트 컨트롤러", description = "테스트")
public class TestController {

    @GetMapping
    @Operation(summary = "case1", description = "case1")
    public ResponseEntity<ApiResponse<TestDto>> case1(
            @RequestBody TestDto testDto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(testDto));
    }

}
