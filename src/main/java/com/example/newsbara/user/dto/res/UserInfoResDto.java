package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResDto {
    private Integer id;
    private String email;
    private String phone;
    private String name;
    private Integer point;
    private String profileImg;

    public static UserInfoResDto fromEntity(User user) {
        return UserInfoResDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .point(user.getPoint())
                .profileImg(user.getProfileImg())
                .build();
    }
}