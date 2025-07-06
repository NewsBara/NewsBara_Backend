// 후순위 개발이지만 미리 만들어둠
package com.example.newsbara.user.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.user.domain.enums.FollowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Follow", uniqueConstraints = {
        @UniqueConstraint(name = "uk_follower_following", columnNames = {"follower_id", "following_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status;

    public void updateStatus(FollowStatus status) {
        this.status = status;
    }
}