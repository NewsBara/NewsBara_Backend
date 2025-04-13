package com.example.newsbara.video.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.history.domain.History;
import com.example.newsbara.test.domain.Test;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Video", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_channel", columnList = "channel")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String thumbnail;

    @Column(nullable = false)
    private Integer length;

    @Column(nullable = false)
    private String channel;

    @Column
    private Integer view;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Test> tests = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<History> histories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.view = this.view == null ? 0 : this.view;
    }
}