package com.example.newsbara.user.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.dto.res.UserInfoResDto;
import com.example.newsbara.user.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/api/mypage")
@Tag(name = "마이페이지 API", description = "마이페이지 관련 기능을 제공하는 API")

public class MypageController {

    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }


    @GetMapping("")
    @Operation(summary = "마이페이지 메인 조회 API",
    description = "마이페이지 메인 화면에 있는 회원 정보를 조회하는 API입니다.")
    public ApiResponse<MypageResDto> getMypage(Principal principal) {

        return ApiResponse.onSuccess(mypageService.getMypage(principal));
    }
}
