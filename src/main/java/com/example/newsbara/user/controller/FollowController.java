package com.example.newsbara.user.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;

import com.example.newsbara.user.dto.req.FollowAddReqDto;
import com.example.newsbara.user.dto.res.FollowAddResDto;
import com.example.newsbara.user.dto.res.FollowResListDto;
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
    @PostMapping("/add")
    @Operation(summary = "친구 신청 API",
            description = "다른 사용자에게 친구 신청을 보내는 API입니다.")
    public ApiResponse<FollowAddResDto> addFollow(
            @RequestBody FollowAddReqDto followReqDto,
            Principal principal) {
        return  ApiResponse.onSuccess(followService.addFollow(principal, followReqDto));
    }

    // 받은 친구 요청 목록
    @GetMapping("/requests")
    @Operation(summary = "친구 요청 목록 API",
            description = "자신에게 온 친구 요청 목록을 확인하는 API입니다.")
    public ApiResponse<List<FollowResListDto>> getRequests(
            Principal principal) {
        return  ApiResponse.onSuccess(followService.getRequests(principal));
    }

}
