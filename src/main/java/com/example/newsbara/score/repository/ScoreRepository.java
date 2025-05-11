package com.example.newsbara.score.repository;

import com.example.newsbara.score.domain.Score;
import com.example.newsbara.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    Optional<Score> findTopByUserOrderByCreatedAtDesc(User user);
}