package com.example.newsbara.user.dto.res;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResDto {
    private Integer userId;
    private String userName;
    private String profileImage;
    private Integer point;
    private boolean isFollowing;
    private boolean isPending;
}
