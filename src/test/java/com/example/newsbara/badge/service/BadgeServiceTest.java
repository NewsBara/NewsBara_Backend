package com.example.newsbara.badge.service;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.badge.dto.res.BadgeResDto;
import com.example.newsbara.badge.repository.BadgeRepository;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private Principal principal;

    private User testUser;
    private Badge bronzeBadge;
    private Badge silverBadge;
    private Badge goldBadge;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .id(1)
                .email("test@example.com")
                .name("테스트유저")
                .point(350)
                .build();

        // 테스트용 뱃지들 생성
        bronzeBadge = Badge.builder()
                .id(1)
                .name("bronze")
                .minPoint(100)
                .maxPoint(499)
                .build();

        silverBadge = Badge.builder()
                .id(2)
                .name("silver")
                .minPoint(500)
                .maxPoint(999)
                .build();

        goldBadge = Badge.builder()
                .id(3)
                .name("gold")
                .minPoint(1000)
                .maxPoint(1999)
                .build();
    }

    @Test
    @DisplayName("뱃지 조회 - 성공")
    void getBadgeInfo_Success() {
        // given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(badgeRepository.findCurrentBadgesByPoints(350)).thenReturn(List.of(bronzeBadge));
        when(badgeRepository.findNextBadgesByPoints(350)).thenReturn(List.of(silverBadge, goldBadge));

        // when
        BadgeResDto result = badgeService.getBadgeInfo(principal);

        // then
        assertThat(result.getCurrentBadgeName()).isEqualTo("bronze");
        assertThat(result.getCurrentPoints()).isEqualTo(350);
        assertThat(result.getNextBadgeMinPoint()).isEqualTo(500);
        assertThat(result.getNextBadgeName()).isEqualTo("silver");
    }

    @Test
    @DisplayName("현재 뱃지가 없는 경우 (포인트가 최소 뱃지보다 적음)")
    void getBadgeInfo_NoCurrent() {
        // given
        testUser.setPoint(50); // 최소 뱃지(100포인트)보다 적음
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(badgeRepository.findCurrentBadgesByPoints(50)).thenReturn(Collections.emptyList());
        when(badgeRepository.findNextBadgesByPoints(50)).thenReturn(List.of(bronzeBadge, silverBadge));

        // when
        BadgeResDto result = badgeService.getBadgeInfo(principal);

        // then
        assertThat(result.getCurrentBadgeName()).isNull();
        assertThat(result.getCurrentPoints()).isEqualTo(50);
        assertThat(result.getNextBadgeMinPoint()).isEqualTo(100);
        assertThat(result.getNextBadgeName()).isEqualTo("bronze");
    }

    @Test
    @DisplayName("다음 뱃지가 없는 경우 (최고 등급 뱃지)")
    void getUserBadgeInfo_NoNextBadge() {
        // given
        testUser.setPoint(2000); // 최고 등급
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(badgeRepository.findCurrentBadgesByPoints(2000)).thenReturn(List.of(goldBadge));
        when(badgeRepository.findNextBadgesByPoints(2000)).thenReturn(Collections.emptyList());

        // when
        BadgeResDto result = badgeService.getBadgeInfo(principal);

        // then
        assertThat(result.getCurrentBadgeName()).isEqualTo("gold");
        assertThat(result.getCurrentPoints()).isEqualTo(2000);
        assertThat(result.getNextBadgeMinPoint()).isNull();
        assertThat(result.getNextBadgeName()).isNull();
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우 - 예외 발생")
    void getUserBadgeInfo_UserNotFound() {
        // given
        when(principal.getName()).thenReturn("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> badgeService.getBadgeInfo(principal))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("뱃지 업데이트 - 성공")
    void updateUserBadge_Success() {
        // given
        testUser.setBadge(null); // 현재 뱃지 없음
        when(badgeRepository.findCurrentBadgesByPoints(350)).thenReturn(List.of(bronzeBadge));

        // when
        badgeService.updateUserBadge(testUser);

        // then
        assertThat(testUser.getBadge()).isEqualTo(bronzeBadge);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("뱃지 업데이트 - 이미 같은 뱃지일 경우")
    void updateUserBadge_SameBadge() {
        // given
        testUser.setBadge(bronzeBadge); // 이미 브론즈 뱃지 보유
        when(badgeRepository.findCurrentBadgesByPoints(350)).thenReturn(List.of(bronzeBadge));

        // when
        badgeService.updateUserBadge(testUser);

        // then
        verify(userRepository, never()).save(testUser);
    }
}