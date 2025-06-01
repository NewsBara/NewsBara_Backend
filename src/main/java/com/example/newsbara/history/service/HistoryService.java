package com.example.newsbara.history.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.dto.req.HistoryReqDto;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.history.repository.HistoryRepository;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class HistoryService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
//    private final YoutubeApiService youtubeApiService;


    public HistoryService(UserRepository userRepository, HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
//        this.youtubeApiService = youtubeApiService;
    }

    @Transactional
    public HistoryResDto postHistory(Principal principal, HistoryReqDto request) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 기존 시청 기록이 있는지 확인
        Optional<WatchHistory> existingHistory = historyRepository
                .findByUserAndVideoId(user, request.getVideoId());

        WatchHistory watchHistory;

        if (existingHistory.isPresent()) {
            // 기존 기록이 있으면 상태만 업데이트
            watchHistory = existingHistory.get();
            watchHistory.updateStatus(request.getStatus());
        } else {
            // 새로운 기록 생성
            watchHistory = request.toEntity();
            watchHistory.setUser(user);

            // 프론트에서 Youtube API에서 받은 형식 그대로 전송할 경우 (PT15M33S)
//          String duration = YoutubeDurationParser.parse(request.getLength());
//          watchHistory.setLength(duration);
        }

        // 저장
        WatchHistory savedHistory = historyRepository.save(watchHistory);

        // 응답 생성
        return HistoryResDto.fromEntity(savedHistory);
    }

    @Transactional
    public List<HistoryResDto> getHistory(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<WatchHistory> histories = historyRepository.findByUserOrderByCreatedAtDesc(user);

        return histories.stream().map(HistoryResDto::fromEntity).collect(Collectors.toList());
    }
}
