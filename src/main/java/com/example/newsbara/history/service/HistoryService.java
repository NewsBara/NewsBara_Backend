package com.example.newsbara.history.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.dto.req.HistoryReqDto;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.history.repository.HistoryRepository;
import com.example.newsbara.history.util.YoutubeDurationParser;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class HistoryService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final YoutubeApiService youtubeApiService;


    public HistoryService(UserRepository userRepository, HistoryRepository historyRepository, YoutubeApiService youtubeApiService) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.youtubeApiService = youtubeApiService;
    }

    @Transactional
    public HistoryResDto postHistory(Principal principal, HistoryReqDto request) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        // 2. YouTube API를 통해 비디오 정보 가져오기
        YoutubeVideoInfo videoInfo = youtubeApiService.getVideoInfo(request.getVideoId());

        int durationInSeconds = YoutubeDurationParser.parseToSeconds(videoInfo.getLength());
        String categoryName = youtubeApiService.getCategoryName(videoInfo.getCategory());

        // 3. WatchHistory 엔티티 생성
        WatchHistory watchHistory = WatchHistory.builder()
                .user(user)
                .videoId(request.getVideoId())
                .title(videoInfo.getTitle())
                .thumbnail(videoInfo.getThumbnail())
                .channel(videoInfo.getChannel())
                .length(durationInSeconds) // 변환된 문자열 사용
                .category(categoryName)   // 실제 카테고리명
                .status(request.getStatus())
                .build();


        // 4. 저장
        WatchHistory savedHistory = historyRepository.save(watchHistory);

        // 5. 응답 생성
        return HistoryResDto.fromEntity(savedHistory);
    }

    @Transactional
    public List<HistoryResDto> getHistory(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<WatchHistory> histories = historyRepository.findByUser(user);

        return histories.stream().map(HistoryResDto::fromEntity).collect(Collectors.toList());
    }
}
