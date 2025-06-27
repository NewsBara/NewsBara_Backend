package com.example.newsbara.user.dto.res;

import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResDto {
    private Integer id;
    @NotNull
    private String profile_url;

    public static ProfileResDto fromEntity(User user) {
        return ProfileResDto.builder()
                .id(user.getId())
                .profile_url(user.getProfileImg())
                .build();
    }
}
