package com.example.newsbara.user.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.enums.FollowStatus;
import com.example.newsbara.user.dto.req.HandleReqDto;
import com.example.newsbara.user.dto.res.FollowAddResDto;
import com.example.newsbara.user.dto.res.FollowResListDto;
import com.example.newsbara.user.dto.res.HandleResDto;
import com.example.newsbara.user.dto.res.SearchResDto;
import com.example.newsbara.user.repository.FollowRepository;
import com.example.newsbara.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService 테스트")
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User following;
    private Follow follow;
    private String userEmail;

    @BeforeEach
    void setUp() {
        userEmail = "test@example.com";

        follower = User.builder()
                .id(1)
                .name("팔로워")
                .email(userEmail)
                .point(100)
                .profileImg("profile1.jpg")
                .build();

        following = User.builder()
                .id(2)
                .name("팔로잉")
                .email("following@example.com")
                .point(200)
                .profileImg("profile2.jpg")
                .build();

        follow = Follow.builder()
                .id(1)
                .follower(follower)
                .following(following)
                .status(FollowStatus.PENDING)
                .build();

        given(principal.getName()).willReturn(userEmail);
    }

    @Test
    @DisplayName("친구 신청 성공")
    void addFollow_Success() {
        // given
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(2)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(follower, following)).willReturn(Optional.empty());
        given(followRepository.findByFollowerAndFollowing(following, follower)).willReturn(Optional.empty());
        given(followRepository.save(any(Follow.class))).willReturn(follow);

        // when
        FollowAddResDto result = followService.addFollow(2, principal);

        // then
        assertThat(result.getFollowingId()).isEqualTo(1);
        assertThat(result.getFollowStatus()).isEqualTo(FollowStatus.PENDING);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신에게 친구 신청 시 예외 발생")
    void addFollow_SelfRequest_ThrowsException() {
        // given
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(1)).willReturn(Optional.of(follower));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.addFollow(1, principal));

        assertEquals(ErrorStatus.CANNOT_ADD_SELF, exception.getCode());

    }

    @Test
    @DisplayName("이미 친구 신청을 보낸 경우 예외 발생")
    void addFollow_DuplicateRequest_ThrowsException() {
        // given
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(2)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(follower, following))
                .willReturn(Optional.of(follow));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.addFollow(2, principal));

        assertEquals(ErrorStatus.DUPLICATE_FRIEND_REQUEST, exception.getCode());
    }

    @Test
    @DisplayName("이미 친구인 경우 예외 발생")
    void addFollow_AlreadyFriends_ThrowsException() {
        // given
        Follow acceptedFollow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(2)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(follower, following))
                .willReturn(Optional.of(acceptedFollow));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.addFollow(2, principal));

        assertEquals(ErrorStatus.ALREADY_FRIENDS, exception.getCode());
    }

    @Test
    @DisplayName("거절된 요청 재신청 성공")
    void addFollow_RejectedRequest_Success() {
        // given
        Follow rejectedFollow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.REJECTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(2)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(follower, following))
                .willReturn(Optional.of(rejectedFollow));
        given(followRepository.save(rejectedFollow)).willReturn(rejectedFollow);

        // when
        FollowAddResDto result = followService.addFollow(2, principal);

        // then
        assertThat(result.getFollowStatus()).isEqualTo(FollowStatus.PENDING);
        verify(followRepository).save(rejectedFollow);
    }

    @Test
    @DisplayName("상대방이 이미 친구 신청을 보낸 경우 예외 발생")
    void addFollow_ReverseRequestExists_ThrowsException() {
        // given
        Follow reverseFollow = Follow.builder()
                .follower(following)
                .following(follower)
                .status(FollowStatus.PENDING)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(2)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(follower, following))
                .willReturn(Optional.empty());
        given(followRepository.findByFollowerAndFollowing(following, follower))
                .willReturn(Optional.of(reverseFollow));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.addFollow(2, principal));

        assertEquals(ErrorStatus.FRIEND_REQUEST_EXISTS, exception.getCode());
    }

    @Test
    @DisplayName("친구 요청 목록 조회 성공")
    void getRequests_Success() {
        // given
        List<Follow> pendingRequests = List.of(follow);
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(followRepository.findPendingRequestsByUser(follower, FollowStatus.PENDING))
                .willReturn(pendingRequests);

        // when
        List<FollowResListDto> result = followService.getRequests(principal);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFollowerName()).isEqualTo("팔로워");
        assertThat(result.get(0).getFollowStatus()).isEqualTo(FollowStatus.PENDING);
    }

    @Test
    @DisplayName("친구 요청 승인 성공")
    void handleRequest_Accept_Success() {
        // given
        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(following));
        given(followRepository.findById(1)).willReturn(Optional.of(follow));
        given(followRepository.findByFollowerAndFollowing(following, follower))
                .willReturn(Optional.empty());
        given(followRepository.save(follow)).willReturn(follow);

        // when
        HandleResDto result = followService.handleRequest(1, request, principal);

        // then
        assertThat(result.getName()).isEqualTo("팔로워");
        assertThat(result.getFollowStatus()).isEqualTo(FollowStatus.ACCEPTED);
        verify(followRepository).save(follow);
    }

    @Test
    @DisplayName("친구 요청 거절 성공")
    void handleRequest_Reject_Success() {
        // given
        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.REJECTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(following));
        given(followRepository.findById(1)).willReturn(Optional.of(follow));
        given(followRepository.save(follow)).willReturn(follow);

        // when
        HandleResDto result = followService.handleRequest(1, request, principal);

        // then
        assertThat(result.getName()).isEqualTo("팔로워");
        assertThat(result.getFollowStatus()).isEqualTo(FollowStatus.REJECTED);
        verify(followRepository).save(follow);
    }

    @Test
    @DisplayName("본인이 아닌 요청 처리 시 예외 발생")
    void handleRequest_Unauthorized_ThrowsException() {
        // given
        User anotherUser = User.builder()
                .id(3)
                .name("다른 사용자")
                .email("another@example.com")
                .build();

        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(anotherUser));
        given(followRepository.findById(1)).willReturn(Optional.of(follow));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.handleRequest(1, request, principal));

        assertEquals(ErrorStatus._UNAUTHORIZED, exception.getCode());
    }

    @Test
    @DisplayName("이미 처리된 요청 재처리 시 예외 발생")
    void handleRequest_AlreadyHandled_ThrowsException() {
        // given
        Follow acceptedFollow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.ACCEPTED)
                .build();

        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(following));
        given(followRepository.findById(1)).willReturn(Optional.of(acceptedFollow));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.handleRequest(1, request, principal));

        assertEquals(ErrorStatus.REQUEST_ALREADY_HANDLED, exception.getCode());
    }

    @Test
    @DisplayName("사용자 검색 성공")
    void searchUsers_Success() {
        // given
        String searchName = "test";
        List<User> searchResults = List.of(following);

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(followRepository.searchUsersByName(searchName, follower)).willReturn(searchResults);
        given(followRepository.findByFollowerAndFollowing(follower, following))
                .willReturn(Optional.empty());
        given(followRepository.findByFollowerAndFollowing(following, follower))
                .willReturn(Optional.empty());

        // when
        List<SearchResDto> result = followService.searchUsers(searchName, principal);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("팔로잉");
        assertThat(result.get(0).isFollowing()).isFalse();
        assertThat(result.get(0).isPending()).isFalse();
    }

    @Test
    @DisplayName("친구 목록 조회 성공")
    void getFriends_Success() {
        // given
        Follow acceptedFollow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.ACCEPTED)
                .build();

        List<Follow> acceptedFollows = List.of(acceptedFollow);
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(followRepository.findAcceptedFollowsByUser(follower, FollowStatus.ACCEPTED))
                .willReturn(acceptedFollows);

        // when
        List<FollowResListDto> result = followService.getFriends(principal);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFollowerName()).isEqualTo("팔로워");
        assertThat(result.get(0).getFollowStatus()).isEqualTo(FollowStatus.ACCEPTED);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 친구 신청 시 예외 발생")
    void addFollow_UserNotFound_ThrowsException() {
        // given
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(userRepository.findById(999)).willReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.addFollow(999, principal));

        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getCode());

    }

    @Test
    @DisplayName("존재하지 않는 요청 처리 시 예외 발생")
    void handleRequest_FollowNotFound_ThrowsException() {
        // given
        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(follower));
        given(followRepository.findById(999)).willReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> followService.handleRequest(999, request, principal));

        assertEquals(ErrorStatus.FOLLOW_NOT_FOUND, exception.getCode());

    }

    @Test
    @DisplayName("친구 요청 승인 시 반대 방향 대기 요청 삭제")
    void handleRequest_Accept_DeleteReversePendingRequest() {
        // given
        Follow reversePendingFollow = Follow.builder()
                .follower(following)
                .following(follower)
                .status(FollowStatus.PENDING)
                .build();

        HandleReqDto request = HandleReqDto.builder()
                .followStatus(FollowStatus.ACCEPTED)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(following));
        given(followRepository.findById(1)).willReturn(Optional.of(follow));
        given(followRepository.findByFollowerAndFollowing(following, follower))
                .willReturn(Optional.of(reversePendingFollow));
        given(followRepository.save(follow)).willReturn(follow);

        // when
        followService.handleRequest(1, request, principal);

        // then
        verify(followRepository).delete(reversePendingFollow);
    }
}