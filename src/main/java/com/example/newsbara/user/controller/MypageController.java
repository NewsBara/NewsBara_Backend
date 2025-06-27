package com.example.newsbara.user.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.user.dto.req.NameReqDto;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.dto.res.NameResDto;
import com.example.newsbara.user.dto.res.ProfileResDto;
import com.example.newsbara.user.dto.res.UserInfoResDto;
import com.example.newsbara.user.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;


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

    @PutMapping("/name")
    @Operation(summary = "이름 수정 API",
            description = "사용자의 이름을 수정하는 API입니다.")
    public ApiResponse<NameResDto> putName(
            Principal principal,
            @RequestBody NameReqDto request) {
        return ApiResponse.onSuccess(mypageService.putName(principal, request));
    }


    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    @Operation(summary = "프로필 사진 수정 API",
            description = "프로필 사진을 수정하는 API입니다.")
    public ApiResponse<ProfileResDto> putProfile(
            Principal principal,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ApiResponse.onSuccess(mypageService.putProfile(principal, file));
    }

}
