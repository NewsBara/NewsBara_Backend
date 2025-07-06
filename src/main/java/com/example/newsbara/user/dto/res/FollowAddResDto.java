package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.enums.FollowStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowAddResDto {
    private Integer followingId;
    private FollowStatus followStatus;

    public static FollowAddResDto fromEntity(Follow follow) {
        return FollowAddResDto.builder()
                .followingId(follow.getId())
                .followStatus(follow.getStatus())
                .build();
    }
}
