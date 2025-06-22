package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MypageResDto {
    private Integer id;
    private String email;
    private String name;
    private String badgeName;
    private Integer point;
    private String profileImg;

    public static MypageResDto fromEntity(User user) {
        return MypageResDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .badgeName(user.getBadge() != null ? user.getBadge().getName() : null)
                .point(user.getPoint())
                .profileImg(user.getProfileImg())
                .build();
    }
}
