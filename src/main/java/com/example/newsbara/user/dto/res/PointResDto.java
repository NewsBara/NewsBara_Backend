package com.example.newsbara.user.dto.res;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointResDto {
    private Integer userId;
    private Integer point;
    private String badgeName;

    public static PointResDto fromEntity(User user) {
        return PointResDto.builder()
                .userId(user.getId())
                .point(user.getPoint())
                .badgeName(user.getBadge() != null ? user.getBadge().getName() : null)                .build();
    }
}
