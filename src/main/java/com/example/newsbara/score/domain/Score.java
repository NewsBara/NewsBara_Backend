package com.example.newsbara.score.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.score.domain.enums.TestType;
import com.example.newsbara.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Score")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType;

    @Column(nullable = false)
    private Integer score;

}