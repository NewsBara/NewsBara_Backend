// 문제를 저장하는 것 없이 매번 생성한다면, TEST 도메인 필요없음
package com.example.newsbara.test.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.test.domain.enums.Level;
import com.example.newsbara.video.domain.Video;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Test", indexes = {
        @Index(name = "idx_level", columnList = "level")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    // 현재는 level에 따라 문제를 저장하기로 되어있음
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Lob
    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Lob
    private String solution;



}