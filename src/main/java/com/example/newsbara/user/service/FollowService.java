package com.example.newsbara.user.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.enums.FollowStatus;
import com.example.newsbara.user.dto.req.FollowAddReqDto;
import com.example.newsbara.user.dto.res.FollowAddResDto;
import com.example.newsbara.user.dto.res.FollowResListDto;
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

    @Transactional
    public FollowAddResDto addFollow(Principal principal, FollowAddReqDto request) {
        User follower = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() ->  new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (follower.getId().equals(following.getId())) {
            throw new GeneralException(ErrorStatus.CANNOT_ADD_SELF);  //IllegalArgumentException("자신에게 친구 신청할 수 없습니다.");
        }

        // 이미 관계가 있는지 확인
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            FollowStatus status = existingFollow.get().getStatus();
            if (status == FollowStatus.PENDING) {
                throw new GeneralException(ErrorStatus.DUPLICATE_FRIEND_REQUEST);
            } else if (status == FollowStatus.ACCEPTED) {
                throw new GeneralException(ErrorStatus.ALREADY_FRIENDS);
            }
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .status(FollowStatus.PENDING)
                .build();

        return FollowAddResDto.fromEntity(followRepository.save(follow));
    }

    @Transactional(readOnly = true)
    public List<FollowResListDto> getRequests(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        List<Follow> pendingRequests = followRepository.findPendingRequestsByUser(user, FollowStatus.PENDING);

        return pendingRequests.stream().map(FollowResListDto::fromEntity).collect(Collectors.toList());
    }
}
