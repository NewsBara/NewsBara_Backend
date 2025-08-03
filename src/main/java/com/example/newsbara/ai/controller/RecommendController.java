package com.example.newsbara.ai.controller;

import com.example.newsbara.ai.dto.res.RecommendResDto;
import com.example.newsbara.ai.service.ExternalApiService;
import com.example.newsbara.ai.service.RecommendService;
import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/recommend")
@Slf4j
@Tag(name = "추천 알고리즘 API", description = "메인 화면의 추천 동영상을 제공하는 API")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/{channelName}")
    @Operation(summary = "메인 화면 추천 동영상 조회 API")
    public ResponseEntity<ApiResponse<RecommendResDto>> getRecommendations(
            @PathVariable String channelName, Principal principal) {

        if (channelName == null || channelName.trim().isEmpty()) {
            log.warn("Empty channelName detected before calling ML server");
            throw new GeneralException(ErrorStatus.CHANNEL_IS_NULL);
        }

        log.info("Received recommendation request for user: {}, channelName: {}", principal.getName(), channelName);

        RecommendResDto responseDto = recommendService.getRecommendations(channelName, principal);

        log.info("Successfully processed recommendation request for user: {}", principal.getName());

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }
}