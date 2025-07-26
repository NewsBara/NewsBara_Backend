package com.example.newsbara.ai.controller;

import com.example.newsbara.ai.dto.req.PronounceReqDto;
import com.example.newsbara.ai.dto.res.PronounceResDto;
import com.example.newsbara.ai.service.ExternalApiService;
import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shadowing")
@Tag(name = "쉐도잉 API", description = "발음 평가를 제공하는 API")
public class ShadowingController {

    private final ExternalApiService externalApiService;

    public ShadowingController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @PostMapping(value = "/evaluate", consumes = {"multipart/form-data"})
    @Operation(summary = "발음평가 API")
    public ResponseEntity<ApiResponse<PronounceResDto>> evaluatePronunciation(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("script") String script) {

        if (audioFile.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_IS_NULL);
        }

        if (script == null || script.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.SCRIPT_IS_NULL);
        }


        return ResponseEntity.ok(ApiResponse.onSuccess(externalApiService.evaluatePronunciation(audioFile, script)));
    }
}
