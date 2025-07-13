package com.example.newsbara.user.dto.req;

import com.example.newsbara.score.domain.Score;
import com.example.newsbara.score.domain.enums.TestType;
import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor  // 이 어노테이션 추가!
@Builder
public class UserSignupReqDto {
    private String email;
    private String password;
    private String phone;
    private String name;
    private List<ScoreDto> scores; // 점수 정보 리스트 추가

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreDto {
        private TestType testType;
        private Integer score;
    }

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .phone(this.phone)
                .name(this.name)
                .point(0)
                .build();
    }

    // Score 엔티티 리스트로 변환하는 메서드
    public List<Score> toScoreEntities(User user) {
        if (scores == null || scores.isEmpty()) {
            return List.of();
        }

        return scores.stream()
                .map(scoreDto -> Score.builder()
                        .user(user)
                        .testType(scoreDto.getTestType())
                        .score(scoreDto.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}