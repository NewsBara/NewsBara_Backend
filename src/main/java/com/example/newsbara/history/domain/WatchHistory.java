package com.example.newsbara.history.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.history.domain.enums.Status;
import com.example.newsbara.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "WatchHistory", indexes = {
        @Index(name = "idx_user_created_at", columnList = "user_id, created_at DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // YouTube 비디오 ID
    @Column(nullable = false)
    private String videoId;

    @Column(nullable = false)
    private String title;

    private String thumbnail;

    @Column(nullable = false)
    private String channel;

    // parse 된 형태로 저장 (00:00:00)
    private String length;

    // 카테고리 데이터 형식 추후 결정
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;


    // WatchHistory 엔티티에 추가
    public void updateStatus(Status status) {
        this.status = status;
        // BaseEntity를 상속-> @LastModifiedDate로 updatedAt이 자동 업데이트됨
    }
}