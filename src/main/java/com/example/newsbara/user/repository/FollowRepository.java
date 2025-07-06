package com.example.newsbara.user.repository;

import com.example.newsbara.user.domain.Follow;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 받은 친구 요청 목록 (최신순)
    @Query("SELECT f FROM Follow f WHERE f.following = :user AND f.status = :status ORDER BY f.createdAt DESC")
    List<Follow> findPendingRequestsByUser(@Param("user") User user, @Param("status") FollowStatus status);

    // 사용자 검색 (이름으로)
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u != :currentUser")
    List<User> searchUsersByName(@Param("name") String name, @Param("currentUser") User currentUser);
}
