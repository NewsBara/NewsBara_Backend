package com.example.newsbara.badge.controller;

import com.example.newsbara.badge.dto.res.BadgeResDto;
import com.example.newsbara.badge.service.BadgeService;
import com.example.newsbara.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/badge")
@Tag(name = "뱃지 API", description = "뱃지 관련 기능을 제공하는 API")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping("")
    @Operation(summary = "뱃지 조회 API")
    public ResponseEntity<ApiResponse<BadgeResDto>> getMyBadge(Principal principal) {

        return ResponseEntity.ok(ApiResponse.onSuccess(badgeService.getBadgeInfo(principal)));
    }
}
