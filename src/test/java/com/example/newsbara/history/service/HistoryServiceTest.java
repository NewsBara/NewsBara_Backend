package com.example.newsbara.history.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.domain.enums.Status;
import com.example.newsbara.history.dto.req.HistoryReqDto;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.history.repository.HistoryRepository;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryServiceTest {

    private UserRepository userRepository;
    private HistoryRepository historyRepository;
    private YoutubeApiService youtubeApiService;
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        historyRepository = mock(HistoryRepository.class);
        youtubeApiService = mock(YoutubeApiService.class);
        historyService = new HistoryService(userRepository, historyRepository, youtubeApiService);
    }

    @Test
    void postHistory_성공() {
        // given
        Principal principal = () -> "test@example.com";

        User user = User.builder()
                .email("test@example.com")
                .build();

        HistoryReqDto request = new HistoryReqDto("video123", Status.WATCHED);

        YoutubeVideoInfo videoInfo = YoutubeVideoInfo.builder()
                .title("테스트 비디오")
                .thumbnail("thumbnail.jpg")
                .channel("테스트 채널")
                .length("PT10M")
                .category("22")
                .build();

        WatchHistory saved = WatchHistory.builder()
                .user(user)
                .videoId("video123")
                .title("테스트 비디오")
                .thumbnail("thumbnail.jpg")
                .channel("테스트 채널")
                .length(600)
                .category("교육")
                .status(Status.WATCHED)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(youtubeApiService.getVideoInfo("video123")).thenReturn(videoInfo);
        when(youtubeApiService.getCategoryName("22")).thenReturn("교육");
        when(historyRepository.save(any(WatchHistory.class))).thenReturn(saved);

        // when
        HistoryResDto result = historyService.postHistory(principal, request);

        // then
        assertEquals("video123", result.getVideoId());
        assertEquals("테스트 비디오", result.getTitle());
        assertEquals(Status.WATCHED, result.getStatus());
        verify(historyRepository, times(1)).save(any());
    }

    @Test
    void getHistory_성공() {
        // given
        Principal principal = () -> "test@example.com";
        User user = User.builder().email("test@example.com").build();

        WatchHistory history = WatchHistory.builder()
                .user(user)
                .videoId("video123")
                .title("Test Video")
                .thumbnail("thumb.jpg")
                .channel("Test Channel")
                .length(300)
                .category("교육")
                .status(Status.WATCHED)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(historyRepository.findByUser(user)).thenReturn(Collections.singletonList(history));

        // when
        var resultList = historyService.getHistory(principal);

        // then
        assertEquals(1, resultList.size());
        assertEquals("video123", resultList.get(0).getVideoId());
        verify(historyRepository, times(1)).findByUser(user);
    }

    @Test
    void postHistory_사용자없을때_예외() {
        // given
        Principal principal = () -> "nonexistent@example.com";
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // when & then
        GeneralException ex = assertThrows(GeneralException.class,
                () -> historyService.postHistory(principal, new HistoryReqDto("video123", Status.WATCHED)));

        assertEquals(ErrorStatus.USER_NOT_FOUND, ex.getCode());
    }
}
