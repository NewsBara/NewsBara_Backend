package com.example.newsbara.badge.repository;

import com.example.newsbara.badge.domain.Badge;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    // 현재 포인트로 획득 가능한 뱃지 중 가장 높은 등급 조회
    @Query("SELECT b FROM Badge b WHERE b.minPoint <= :currentPoints ORDER BY b.minPoint DESC")
    List<Badge> findCurrentBadgesByPoints(@Param("currentPoints") Integer currentPoints);

    // 다음 뱃지 조회 (현재 포인트보다 높은 최소 포인트의 뱃지)
    @Query("SELECT b FROM Badge b WHERE b.minPoint > :currentPoints ORDER BY b.minPoint ASC")
    List<Badge> findNextBadgesByPoints(@Param("currentPoints") Integer currentPoints);
}
