package com.example.newsbara.user.dto.req;

import com.example.newsbara.user.domain.enums.FollowStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class HandleReqDto {
    private FollowStatus followStatus;
}
