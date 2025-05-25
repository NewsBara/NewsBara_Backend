package com.example.newsbara.history.repository;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.dto.res.HistoryResDto;
import com.example.newsbara.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUser(User user);
}
