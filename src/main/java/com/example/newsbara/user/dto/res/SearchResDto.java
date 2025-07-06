package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.enums.FollowStatus;
import lombok.*;

import java.util.Optional;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResDto {
    private Integer userId;
    private String userName;
    private String profileImage;
    private boolean isFollowing;
    private boolean isPending;
    private boolean isSentByMe;      // 내가 보낸 요청
    private boolean isReceivedByMe;  // 받은 요청

    public static SearchResDto fromEntity(User user, User currentUser, Optional<Follow> followRelation, Optional<Follow> reverseRelation) {
        boolean isFollowing = false;
        boolean isPending = false;
        boolean isSentByMe = false;
        boolean isReceivedByMe = false;

        // 둘 중 하나라도 ACCEPTED이면 친구 상태
        if ((followRelation.isPresent() && followRelation.get().getStatus() == FollowStatus.ACCEPTED) ||
                (reverseRelation.isPresent() && reverseRelation.get().getStatus() == FollowStatus.ACCEPTED)) {
            isFollowing = true;
        }

        // 내가 보낸 요청 (currentUser → user)
        if (followRelation.isPresent() && followRelation.get().getStatus() == FollowStatus.PENDING) {
            isPending = true;
            isSentByMe = true;
        }

        // 받은 요청 (user → currentUser)
        if (reverseRelation.isPresent() && reverseRelation.get().getStatus() == FollowStatus.PENDING) {
            isPending = true;
            isReceivedByMe = true;
        }

        return SearchResDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .profileImage(user.getProfileImg())
                .isFollowing(isFollowing)
                .isPending(isPending)
                .isSentByMe(isSentByMe)
                .isReceivedByMe(isReceivedByMe)
                .build();
    }
}