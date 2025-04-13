// 후순위 개발이지만 미리 만들어둠
package com.example.newsbara.rank.domain;

import com.example.newsbara.global.common.BaseEntity;
import com.example.newsbara.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "UserRank", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_month", columnNames = {"user_id", "month"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate month;

    @Column(nullable = false)
    private Integer point;

    @PrePersist
    public void prePersist() {
        this.point = this.point == null ? 0 : this.point;
    }
}