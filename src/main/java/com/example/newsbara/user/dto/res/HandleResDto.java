package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.enums.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HandleResDto {
    private Integer id;
    private String name;
    private FollowStatus followStatus;

    public static HandleResDto fromEntity(Follow follow) {
        return HandleResDto.builder()
                .id(follow.getId())
                .name(follow.getFollower().getName())
                .followStatus(follow.getStatus())
                .build();
    }
}
