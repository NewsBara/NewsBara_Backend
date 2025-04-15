package com.example.newsbara.user.domain;

import com.example.newsbara.badge.domain.Badge;
import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.rank.domain.Rank;
import com.example.newsbara.score.domain.Score;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private String profileImg;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WatchHistory> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Rank> ranks = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.point = this.point == null ? 0 : this.point;
    }
}