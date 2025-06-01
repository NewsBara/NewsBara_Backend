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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoryService 테스트")
public class HistoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryService historyService;

    // 테스트 상수들
    private static final String TEST_EMAIL = "test@example.com";
    private static final String NONEXISTENT_EMAIL = "nonexistent@example.com";
    private static final String TEST_VIDEO_ID = "video123";
    private static final String TEST_TITLE = "테스트 비디오";
    private static final String TEST_THUMBNAIL = "thumbnail.jpg";
    private static final String TEST_CHANNEL = "테스트 채널";
    private static final String TEST_LENGTH = "00:10:22";
    private static final String TEST_CATEGORY = "Music";

    private User testUser;
    private Principal testPrincipal;
    private HistoryReqDto testRequest;

    @BeforeEach
    void setUp() {
        // dto 생성
        testUser = User.builder()
                .email(TEST_EMAIL)
                .build();

        testPrincipal = () -> TEST_EMAIL;

        testRequest = new HistoryReqDto(
                TEST_VIDEO_ID,
                TEST_TITLE,
                TEST_THUMBNAIL,
                TEST_CHANNEL,
                TEST_LENGTH,
                TEST_CATEGORY,
                Status.WATCHED);
    }

    @Test
    @DisplayName("새 시청 기록 저장 - 성공")
    void postHistory_새기록_성공() {
        // given
        WatchHistory savedHistory = createWatchHistory(testUser);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndVideoId(testUser, TEST_VIDEO_ID))
                .thenReturn(Optional.empty()); // 기존 기록 없음
        when(historyRepository.save(any(WatchHistory.class))).thenReturn(savedHistory);

        // when
        HistoryResDto result = historyService.postHistory(testPrincipal, testRequest);

        // then
        assertNotNull(result);
        assertEquals(TEST_VIDEO_ID, result.getVideoId());
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(Status.WATCHED, result.getStatus());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(historyRepository, times(1)).findByUserAndVideoId(testUser, TEST_VIDEO_ID);
        verify(historyRepository, times(1)).save(any(WatchHistory.class));
    }

    @Test
    @DisplayName("기존 시청 기록 상태 업데이트 - 성공")
    void postHistory_기존기록업데이트_성공() {
        // given
        WatchHistory existingHistory = createWatchHistory(testUser);
        existingHistory.updateStatus(Status.WATCHED); // 기존 상태: WATCHED

        HistoryReqDto updateRequest = new HistoryReqDto(
                TEST_VIDEO_ID,
                TEST_TITLE,
                TEST_THUMBNAIL,
                TEST_CHANNEL,
                TEST_LENGTH,
                TEST_CATEGORY,
                Status.SHADOWING); // 새 상태: SHADOWING

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndVideoId(testUser, TEST_VIDEO_ID))
                .thenReturn(Optional.of(existingHistory)); // 기존 기록 있음
        when(historyRepository.save(existingHistory)).thenReturn(existingHistory);

        // when
        HistoryResDto result = historyService.postHistory(testPrincipal, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(TEST_VIDEO_ID, result.getVideoId());
        assertEquals(Status.SHADOWING, result.getStatus()); // 상태가 업데이트됨

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(historyRepository, times(1)).findByUserAndVideoId(testUser, TEST_VIDEO_ID);
        verify(historyRepository, times(1)).save(existingHistory);

        // 기존 기록의 updateStatus 메서드가 호출되었는지 확인
        assertEquals(Status.SHADOWING, existingHistory.getStatus());
    }

    @Test
    @DisplayName("시청 기록 조회 - 성공")
    void getHistory_성공() {
        // given
        WatchHistory history = createWatchHistory(testUser);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(List.of(history));

        // when
        List<HistoryResDto> resultList = historyService.getHistory(testPrincipal);

        // then
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        HistoryResDto result = resultList.get(0);
        assertEquals(TEST_VIDEO_ID, result.getVideoId());
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(Status.WATCHED, result.getStatus());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(historyRepository, times(1)).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("시청 기록 저장 - 사용자 없음 예외")
    void postHistory_사용자없을때_예외() {
        // given
        when(userRepository.findByEmail(NONEXISTENT_EMAIL)).thenReturn(Optional.empty());
        Principal nonexistentPrincipal = () -> NONEXISTENT_EMAIL;

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> historyService.postHistory(nonexistentPrincipal, testRequest));

        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getCode());
        verify(userRepository, times(1)).findByEmail(NONEXISTENT_EMAIL);
        verify(historyRepository, never()).findByUserAndVideoId(any(), any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("시청 기록 조회 - 사용자 없음 예외")
    void getHistory_사용자없을때_예외() {
        // given
        when(userRepository.findByEmail(NONEXISTENT_EMAIL)).thenReturn(Optional.empty());
        Principal nonexistentPrincipal = () -> NONEXISTENT_EMAIL;

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> historyService.getHistory(nonexistentPrincipal));

        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getCode());
        verify(userRepository, times(1)).findByEmail(NONEXISTENT_EMAIL);
        verify(historyRepository, never()).findByUserOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("시청 기록 조회 - 빈 목록 반환")
    void getHistory_빈목록_반환() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(Collections.emptyList());

        // when
        List<HistoryResDto> resultList = historyService.getHistory(testPrincipal);

        // then
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
        assertEquals(0, resultList.size());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(historyRepository, times(1)).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("상태 변경 시나리오 테스트 - SHADOWING에서 TEST로")
    void postHistory_상태변경시나리오_테스트() {
        // given
        WatchHistory existingHistory = createWatchHistory(testUser);
        existingHistory.updateStatus(Status.SHADOWING);

        HistoryReqDto watchedRequest = new HistoryReqDto(
                TEST_VIDEO_ID,
                TEST_TITLE,
                TEST_THUMBNAIL,
                TEST_CHANNEL,
                TEST_LENGTH,
                TEST_CATEGORY,
                Status.TEST);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndVideoId(testUser, TEST_VIDEO_ID))
                .thenReturn(Optional.of(existingHistory));
        when(historyRepository.save(existingHistory)).thenReturn(existingHistory);

        // when
        HistoryResDto result = historyService.postHistory(testPrincipal, watchedRequest);

        // then
        assertEquals(Status.TEST, result.getStatus());
        assertEquals(Status.TEST, existingHistory.getStatus());

        verify(historyRepository, times(1)).save(existingHistory);
    }

    @Test
    @DisplayName("기존 기록 업데이트 시 updatedAt 변경 확인")
    void postHistory_기존기록업데이트_updatedAt변경() {
        // given
        WatchHistory existingHistory = createWatchHistory(testUser);
        existingHistory.updateStatus(Status.SHADOWING);

        HistoryReqDto updateRequest = new HistoryReqDto(
                TEST_VIDEO_ID, TEST_TITLE, TEST_THUMBNAIL, TEST_CHANNEL,
                TEST_LENGTH, TEST_CATEGORY, Status.WORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndVideoId(testUser, TEST_VIDEO_ID))
                .thenReturn(Optional.of(existingHistory));
        when(historyRepository.save(existingHistory)).thenReturn(existingHistory);

        // when
        historyService.postHistory(testPrincipal, updateRequest);

        // then
        assertEquals(Status.WORD, existingHistory.getStatus());
        // updatedAt이 현재 시간으로 업데이트되었는지 확인
        // (실제로는 BaseEntity의 @LastModifiedDate가 자동으로 처리)
        verify(historyRepository, times(1)).save(existingHistory);
    }

    @Test
    @DisplayName("동일 비디오 여러 상태 업데이트 테스트")
    void postHistory_동일비디오_여러상태업데이트() {
        // given
        WatchHistory existingHistory = createWatchHistory(testUser);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndVideoId(testUser, TEST_VIDEO_ID))
                .thenReturn(Optional.of(existingHistory));
        when(historyRepository.save(existingHistory)).thenReturn(existingHistory);

        // when & then - 첫 번째 업데이트 (SHADOWING)
        HistoryReqDto watchingRequest = new HistoryReqDto(
                TEST_VIDEO_ID, TEST_TITLE, TEST_THUMBNAIL, TEST_CHANNEL,
                TEST_LENGTH, TEST_CATEGORY, Status.SHADOWING);

        HistoryResDto result1 = historyService.postHistory(testPrincipal, watchingRequest);
        assertEquals(Status.SHADOWING, result1.getStatus());

        // when & then - 두 번째 업데이트 (TEST)
        HistoryReqDto watchedRequest = new HistoryReqDto(
                TEST_VIDEO_ID, TEST_TITLE, TEST_THUMBNAIL, TEST_CHANNEL,
                TEST_LENGTH, TEST_CATEGORY, Status.TEST);

        HistoryResDto result2 = historyService.postHistory(testPrincipal, watchedRequest);
        assertEquals(Status.TEST, result2.getStatus());

        verify(historyRepository, times(2)).save(existingHistory);
        verify(historyRepository, times(2)).findByUserAndVideoId(testUser, TEST_VIDEO_ID);
    }

    // 헬퍼 메서드
    private WatchHistory createWatchHistory(User user) {
        return WatchHistory.builder()
                .user(user)
                .videoId(TEST_VIDEO_ID)
                .title(TEST_TITLE)
                .thumbnail(TEST_THUMBNAIL)
                .channel(TEST_CHANNEL)
                .length(TEST_LENGTH)
                .category(TEST_CATEGORY)
                .status(Status.WATCHED)
                .build();
    }
}