package com.example.newsbara.user.service;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.badge.service.BadgeService;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.dto.req.PointReqDto;
import com.example.newsbara.user.dto.res.PointResDto;
import com.example.newsbara.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeService badgeService;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Badge testBadge;
    private Badge nextBadge;

    @BeforeEach
    void setUp() {
        // 테스트용 Badge 생성
        testBadge = Badge.builder()
                .id(1)
                .name("level 1")
                .minPoint(1)
                .maxPoint(99)
                .build();

        nextBadge = Badge.builder()
                .id(2)
                .name("level 2")
                .minPoint(100)
                .maxPoint(399)
                .build();

        // 테스트용 User 생성
        testUser = User.builder()
                .id(1)
                .email("test@example.com")
                .password("encodedPassword")
                .name("testUser")
                .point(50)
                .badge(testBadge)
                .build();
    }

    @Test
    @DisplayName("정답일 때 포인트 20점 추가")
    void addPoint_Correct_Answer() {
        // given
        PointReqDto correctRequest = PointReqDto.builder()
                .isCorrect(true)
                .build();

        given(principal.getName()).willReturn("test@example.com");
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        PointResDto result = userService.addPoint(principal, correctRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getPoint()).isEqualTo(70); // 50 + 20
        assertThat(result.getBadgeName()).isEqualTo("level 1");

        verify(userRepository).findByEmail("test@example.com");
        verify(badgeService).updateUserBadge(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("오답일 때 포인트 10점 추가")
    void addPoint_Wrong_Answer() {
        // given
        PointReqDto wrongRequest = PointReqDto.builder()
                .isCorrect(false)
                .build();

        given(principal.getName()).willReturn("test@example.com");
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        PointResDto result = userService.addPoint(principal, wrongRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getPoint()).isEqualTo(60); // 50 + 10
        assertThat(result.getBadgeName()).isEqualTo("level 1");

        verify(userRepository).findByEmail("test@example.com");
        verify(badgeService).updateUserBadge(testUser);
        verify(userRepository).save(testUser);
    }


    @Test
    @DisplayName("존재하지 않는 사용자 포인트 추가 실패")
    void addPoint_User_Not_Found() {
        // given
        PointReqDto request = PointReqDto.builder()
                .isCorrect(true)
                .build();

        given(principal.getName()).willReturn("test@example.com");
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.addPoint(principal, request));

        assertThat(exception.getCode()).isEqualTo(ErrorStatus.USER_NOT_FOUND);

        verify(userRepository).findByEmail("test@example.com");
        verify(badgeService, never()).updateUserBadge(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("포인트 0인 사용자의 정답 포인트 추가")
    void addPoint_Zero_Point_User_Correct() {
        // given
        testUser.setPoint(0); // 초기 포인트를 0으로 설정
        PointReqDto request = PointReqDto.builder()
                .isCorrect(true)
                .build();

        given(principal.getName()).willReturn("test@example.com");
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        PointResDto result = userService.addPoint(principal, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getPoint()).isEqualTo(20); // 0 + 20
        assertThat(result.getBadgeName()).isEqualTo("level 1");

        verify(userRepository).findByEmail("test@example.com");
        verify(badgeService).updateUserBadge(testUser);
        verify(userRepository).save(testUser);
    }

}