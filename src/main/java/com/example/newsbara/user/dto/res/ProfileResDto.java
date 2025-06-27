package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResDto {
    private Integer id;
    private String profile_url;

    public static ProfileResDto fromEntity(User user) {
        return ProfileResDto.builder()
                .id(user.getId())
                .profile_url(user.getProfileImg())
                .build();
    }
}
