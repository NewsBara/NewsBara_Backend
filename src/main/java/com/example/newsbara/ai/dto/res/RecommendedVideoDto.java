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

        String timeStr = length.trim();

        try {
            // 이미 HH:MM:SS 형태인지 확인
            if (timeStr.matches("^\\d{2}:\\d{2}:\\d{2}$")) {
                return timeStr;
            }

            // MM:SS 형태를 00:MM:SS로 변환
            if (timeStr.matches("^\\d{1,2}:\\d{2}$")) {
                String[] parts = timeStr.split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                return String.format("00:%02d:%02d", minutes, seconds);
            }

            // H:MM:SS 형태를 HH:MM:SS로 변환
            if (timeStr.matches("^\\d{1,2}:\\d{2}:\\d{2}$")) {
                String[] parts = timeStr.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }

        } catch (Exception e) {
            // 파싱 실패시 원본 반환
            return length;
        }

        // 패턴에 맞지 않으면 원본 반환
        return length;
    }

}