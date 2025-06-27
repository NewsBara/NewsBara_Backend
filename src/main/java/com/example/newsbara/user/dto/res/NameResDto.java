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
public class NameResDto {
    private Integer id;
    private String name;

    public static NameResDto fromEntity(User user) {
        return NameResDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
