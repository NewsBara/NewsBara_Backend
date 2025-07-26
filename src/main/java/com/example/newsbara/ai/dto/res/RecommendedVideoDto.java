package com.example.newsbara.ai.dto.res;

import lombok.*;

import java.time.Duration;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedVideoDto {
    private String videoId;
    private String title;
    private String thumbnail;
    private String length;
    private String channel;
    private String category;

    // length getter를 오버라이드하여 자동 변환
    public String getLength() {
        if (length == null || length.trim().isEmpty()) {
            return "00:00:00";
        }

        try {
            Duration duration = Duration.parse(length);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (DateTimeParseException e) {
            // 파싱 실패시 원본 반환 또는 기본값
            return "00:00:00";
        }
    }

}