package com.example.newsbara.history.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.history.domain.enums.Status;
import com.example.newsbara.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "WatchHistory", indexes = {
        @Index(name = "idx_video_id", columnList = "videoId"),
        @Index(name = "idx_title", columnList = "title")
})
@Getter
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

    private Integer length;

    // 카테고리 데이터 형식 추후 결정
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

}