package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.enums.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResListDto {
    private Integer id;
    private Integer followerId;
    private String followerName;
    private Integer followerPoint;
    private String followerProfileImage;
    private FollowStatus followStatus;

    public static FollowResListDto fromEntity(Follow follow) {
        return FollowResListDto.builder()
                .id(follow.getId())
                .followerId(follow.getFollower().getId())
                .followerName(follow.getFollower().getName())
                .followerPoint(follow.getFollower().getPoint())
                .followerProfileImage(follow.getFollower().getProfileImg())
                .followStatus(follow.getStatus())
                .build();
    }
}


