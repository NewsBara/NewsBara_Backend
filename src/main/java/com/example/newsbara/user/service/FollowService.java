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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newsbara.user.repository.UserRepository;


import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    // 친구 신청
    @Transactional
    public FollowAddResDto addFollow(Integer userId, Principal principal) {
        User follower = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        User following = userRepository.findById(userId)
                .orElseThrow(() ->  new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (follower.getId().equals(following.getId())) {
            throw new GeneralException(ErrorStatus.CANNOT_ADD_SELF);  //IllegalArgumentException("자신에게 친구 신청할 수 없습니다.");
        }

        // 1. 같은 방향 체크 (A→B)
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            Follow existFollow = existingFollow.get();
            if (existFollow.getStatus() == FollowStatus.PENDING) {
                throw new GeneralException(ErrorStatus.DUPLICATE_FRIEND_REQUEST);
            } else if (existFollow.getStatus() == FollowStatus.ACCEPTED) {
                throw new GeneralException(ErrorStatus.ALREADY_FRIENDS);
            } else if (existFollow.getStatus() == FollowStatus.REJECTED) {
                // 거절된 상태면 재신청 허용
                existFollow.updateStatus(FollowStatus.PENDING);
                return FollowAddResDto.fromEntity(followRepository.save(existFollow));
            }
        }

        // 2. 반대 방향 체크 (B→A)
        Optional<Follow> reverseFollow = followRepository.findByFollowerAndFollowing(following, follower);
        if (reverseFollow.isPresent()) {
            Follow reverseFollowEntity = reverseFollow.get();
            if (reverseFollowEntity.getStatus() == FollowStatus.PENDING) {
                throw new GeneralException(ErrorStatus.FRIEND_REQUEST_EXISTS); // "상대방이 이미 친구 신청을 보냈습니다"
            } else if (reverseFollowEntity.getStatus() == FollowStatus.ACCEPTED) {
                throw new GeneralException(ErrorStatus.ALREADY_FRIENDS);
            }
            // REJECTED 상태면 신청 허용
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.PENDING)
                .build();

        return FollowAddResDto.fromEntity(followRepository.save(follow));
    }

    // 친구 요청 목록
    @Transactional(readOnly = true)
    public List<FollowResListDto> getRequests(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Follow> pendingRequests = followRepository.findPendingRequestsByUser(user, FollowStatus.PENDING);

        return pendingRequests.stream().map(FollowResListDto::fromEntity).collect(Collectors.toList());
    }

    // 친구 요청 처리
    @Transactional
    public HandleResDto handleRequest(Integer requestId, HandleReqDto request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Follow follow = followRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FOLLOW_NOT_FOUND));

        // 자기 자신에게 요청
        if (!follow.getFollowing().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        // 상태에 따른 예외처리
        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new GeneralException(ErrorStatus.REQUEST_ALREADY_HANDLED);
        }
        if (request.getFollowStatus() == FollowStatus.ACCEPTED) {
            follow.updateStatus(FollowStatus.ACCEPTED);

            // 반대 방향 PENDING 요청이 있으면 삭제
            User follower = follow.getFollower();
            User following = follow.getFollowing();

            Optional<Follow> reverseFollow = followRepository.findByFollowerAndFollowing(following, follower);
            reverseFollow.ifPresent(followRepository::delete);
        } else if (request.getFollowStatus() == FollowStatus.REJECTED) {
            follow.updateStatus(FollowStatus.REJECTED);
        }

        return HandleResDto.fromEntity(followRepository.save(follow));
    }

    // 사용자 검색 (개선 버전)
    @Transactional(readOnly = true)
    public List<SearchResDto> searchUsers(String name, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<User> users = followRepository.searchUsersByName(name, currentUser);

        return users.stream()
                .map(user -> {
                    Optional<Follow> followRelation = followRepository.findByFollowerAndFollowing(currentUser, user);
                    Optional<Follow> reverseRelation = followRepository.findByFollowerAndFollowing(user, currentUser);

                    return SearchResDto.fromEntity(user, currentUser, followRelation, reverseRelation);
                })
                .collect(Collectors.toList());
    }
    // 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FollowResListDto> getFriends(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Follow> follows = followRepository.findAcceptedFollowsByUser(user, FollowStatus.ACCEPTED);

        return follows.stream()
                .map(f -> FollowResListDto.fromEntity(f, user))  // ✅ user 넘김
                .collect(Collectors.toList());
    }
}
