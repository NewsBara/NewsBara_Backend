package com.example.newsbara.badge.service;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.badge.dto.res.BadgeResDto;
import com.example.newsbara.badge.repository.BadgeRepository;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class BadgeService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public BadgeService(UserRepository userRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    public BadgeResDto getBadgeInfo(Principal principal) {

        // 1. 사용자 조회
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Integer currentPoints = user.getPoint();

        // 2. 현재 뱃지 조회
        Badge currentBadge = getCurrentBadge(currentPoints);

        // 3. 다음 뱃지 조회
        Badge nextBadge = getNextBadge(currentPoints);

        // 4. 응답 생성
        return BadgeResDto.builder()
                .currentBadgeName(currentBadge != null ? currentBadge.getName() : null)
                .currentPoints(currentPoints)
                .nextBadgeMinPoint(nextBadge != null ? nextBadge.getMinPoint() : null)
                .nextBadgeName(nextBadge != null ? nextBadge.getName() : null)
                .build();
    }


    private Badge getCurrentBadge(Integer currentPoints) {
        List<Badge> eligibleBadges = badgeRepository.findCurrentBadgesByPoints(currentPoints);
        return eligibleBadges.isEmpty() ? null : eligibleBadges.get(0);
    }

    private Badge getNextBadge(Integer currentPoints) {
        List<Badge> nextBadges = badgeRepository.findNextBadgesByPoints(currentPoints);
        return nextBadges.isEmpty() ? null : nextBadges.get(0);
    }

    // 사용자의 뱃지 업데이트 (포인트 획득 시 호출)
    @Transactional
    public void updateUserBadge(User user) {
        Badge newBadge = getCurrentBadge(user.getPoint());

        // 현재 뱃지와 다른 경우에만 업데이트
        if (newBadge != null &&
                (user.getBadge() == null || !user.getBadge().getId().equals(newBadge.getId()))) {
            user.setBadge(newBadge);
            userRepository.save(user);
        }
    }
}
