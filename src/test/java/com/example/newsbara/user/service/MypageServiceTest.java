package com.example.newsbara.user.service;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MypageService 단위 테스트")
class MypageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private MypageService mypageService;

    private User testUser;
    private Badge testBadge;

    @BeforeEach
    void setUp() {
        // 테스트용 뱃지들 생성
        testBadge = Badge.builder()
                .id(1)
                .name("level1")
                .minPoint(100)
                .maxPoint(499)
                .build();

        testUser = User.builder()
                .id(1)
                .email("test@example.com")
                .name("테스트 사용자")
                .phone("010-1234-5678")
                .point(200)
                .profileImg("test-profile.jpg")
                .password("password")
                .build();


    }

    @Test
    @DisplayName("마이페이지 조회 성공 테스트")
    void getMypage_Success() {
        testUser.setBadge(testBadge);
        // given
        String email = "test@example.com";
        given(principal.getName()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

        // when
        MypageResDto result = mypageService.getMypage(principal);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getName()).isEqualTo(testUser.getName());
        assertThat(result.getPoint()).isEqualTo(testUser.getPoint());
        assertThat(result.getBadgeName()).isEqualTo(testUser.getBadge().getName());
        assertThat(result.getProfileImg()).isEqualTo(testUser.getProfileImg());

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 마이페이지 조회 시 예외 발생")
    void getMypage_UserNotFound_ThrowsException() {
        testUser.setBadge(testBadge);
        // given
        String email = "nonexistent@example.com";
        given(principal.getName()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> mypageService.getMypage(principal));

        assertThat(exception.getCode()).isEqualTo(ErrorStatus.USER_NOT_FOUND);


        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Principal이 null인 경우 예외 발생")
    void getMypage_NullPrincipal_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> mypageService.getMypage(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Principal의 이름이 null인 경우 예외 발생")
    void getMypage_NullPrincipalName_ThrowsException() {
        // given
        given(principal.getName()).willReturn(null);

        // when & then
        assertThatThrownBy(() -> mypageService.getMypage(principal))
                .isInstanceOf(Exception.class);


        verify(userRepository).findByEmail(null);
    }


    @Test
    @DisplayName("프로필 사진이 null인 경우")
    void getMypage_ProfileImg_Null() {
        testUser.setBadge(testBadge);
        testUser.setProfileImg(null);
        // given
        String email = "test@example.com";
        given(principal.getName()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

        // when
        MypageResDto result = mypageService.getMypage(principal);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getName()).isEqualTo(testUser.getName());
        assertThat(result.getPoint()).isEqualTo(testUser.getPoint());
        assertThat(result.getBadgeName()).isEqualTo(testUser.getBadge().getName());
        assertThat(result.getProfileImg()).isEqualTo(null);

        verify(userRepository).findByEmail(email);
    }


}