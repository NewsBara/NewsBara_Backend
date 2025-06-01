package com.example.newsbara.history.repository;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUser(User user);

    List<WatchHistory> findByUserOrderByCreatedAtDesc(User user);

    Optional<WatchHistory> findByUserAndVideoId(User user, String videoId);
}
