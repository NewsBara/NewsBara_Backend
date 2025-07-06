package com.example.newsbara.user.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;

import com.example.newsbara.user.dto.req.HandleReqDto;
import com.example.newsbara.user.dto.res.FollowAddResDto;
import com.example.newsbara.user.dto.res.FollowResListDto;
import com.example.newsbara.user.dto.res.HandleResDto;
import com.example.newsbara.user.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // 친구 신청
    @PostMapping("/{userId}/add")
    @Operation(summary = "친구 신청 API",
            description = "다른 사용자에게 친구 신청을 보내는 API입니다.")
    public ApiResponse<FollowAddResDto> addFollow(
            @PathVariable Integer userId,
            Principal principal) {
        return  ApiResponse.onSuccess(followService.addFollow(userId, principal));
    }

    // 받은 친구 요청 목록
    @GetMapping("/requests")
    @Operation(summary = "친구 요청 목록 API",
            description = "자신에게 온 친구 요청 목록을 확인하는 API입니다.")
    public ApiResponse<List<FollowResListDto>> getRequests(
            Principal principal) {
        return  ApiResponse.onSuccess(followService.getRequests(principal));
    }

    // 친구 요청 처리 (수락/거절)
    @PatchMapping("/requests/{requestId}")
    @Operation(summary = "친구 수락/거절 API",
            description = "자신에게 온 친구 요청을 거절/수락하는 API입니다.")
    public ApiResponse<HandleResDto> handleRequest(
            @PathVariable Long requestId,
            @RequestBody HandleReqDto request,
            Principal principal) {

        return ApiResponse.onSuccess(followService.handleRequest(requestId, request, principal));
    }
}
