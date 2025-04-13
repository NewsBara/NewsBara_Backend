package com.example.newsbara.history.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.history.domain.enums.Status;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.video.domain.Video;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "History", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_video", columnNames = {"user_id", "video_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}