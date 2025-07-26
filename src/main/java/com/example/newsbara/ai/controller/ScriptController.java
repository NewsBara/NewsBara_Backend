package com.example.newsbara.ai.controller;

import com.example.newsbara.ai.dto.req.AnalysisReqDto;
import com.example.newsbara.ai.dto.res.AnalysisResDto;
import com.example.newsbara.ai.service.ExternalApiService;
import com.example.newsbara.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/script")
@Tag(name = "스크립트 API", description = "유튜브 영상의 스크립트 및 해석, 단어 뜻을 제공하는 API")
public class ScriptController {

    private final ExternalApiService externalApiService;

    public ScriptController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/{videoId}")
    @Operation(summary = "스크립트 및 다의어 조회 API")
    public ResponseEntity<ApiResponse<List<AnalysisResDto>>> analyzeVideoByPath(
            @PathVariable String videoId) {

        return ResponseEntity.ok(ApiResponse.onSuccess(externalApiService.analyzeVideo(videoId)));
    }
}
