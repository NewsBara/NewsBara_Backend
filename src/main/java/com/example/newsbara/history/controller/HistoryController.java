package com.example.newsbara.history.controller;

import com.example.newsbara.global.common.apiPayload.ApiResponse;
import com.example.newsbara.history.dto.req.HistoryReqDto;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@Tag(name = "학습 기록 API", description = "학습 기록 관련 기능을 제공하는 API")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }


    @PostMapping("/save")
    @Operation(summary = "학습 진행 사항 저장 API",
    description = "학습 진도 상황에 대한 정보와 시청한 동영상에 대한 정보를 저장합니다.")
    public ResponseEntity<ApiResponse<HistoryResDto>> postHistory(Principal principal, @RequestBody HistoryReqDto request) {

        return ResponseEntity.ok(ApiResponse.onSuccess(historyService.postHistory(principal, request)));
    }

    @GetMapping("")
    @Operation(summary = "학습 진행 사항 조회 API",
            description = "학습 진도 상황에 대한 정보와 시청한 동영상에 대한 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<List<HistoryResDto>>> getHistory(Principal principal) {

        return ResponseEntity.ok(ApiResponse.onSuccess(historyService.getHistory(principal)));
    }
}
