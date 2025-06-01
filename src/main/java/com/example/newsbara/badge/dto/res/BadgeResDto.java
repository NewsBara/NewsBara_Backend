package com.example.newsbara.badge.dto.res;

import com.example.newsbara.badge.domain.Badge;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BadgeResDto {
    private String currentBadgeName;
    private Integer currentPoints;
    private Integer nextBadgeMinPoint;
    private String nextBadgeName;
}
