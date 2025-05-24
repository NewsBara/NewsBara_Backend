package com.example.newsbara.test.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.test.dto.res.TestResDto;
import com.example.newsbara.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tests")
@Tag(name = "테스트 API", description = "테스트 관련 기능을 제공하는 API")

public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/generate/{videoId}")
    @Operation(summary = "테스트 생성 API",
            description = "스트립트에 대한 요약문 빈칸 문제 생성 API입니다. 요약문, 답, 해설을 반환합니다.")
    public ResponseEntity<ApiResponse<TestResDto.TestResponse>> generateTest(Principal principal,
            @PathVariable String videoId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(testService.generateTest(principal, videoId)));
    }
}