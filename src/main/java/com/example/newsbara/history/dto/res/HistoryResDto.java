package com.example.newsbara.history.dto.res;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResDto {

    private Integer id;
    private String videoId;
    private String title;
    private String thumbnail;
    private String channel;
    private String length;
    private String category;
    private Status status;
    private LocalDateTime createdAt;

    public static HistoryResDto fromEntity(WatchHistory watchHistory) {
        return HistoryResDto.builder()
                .id(watchHistory.getId())
                .videoId(watchHistory.getVideoId())
                .title(watchHistory.getTitle())
                .thumbnail(watchHistory.getThumbnail())
                .channel(watchHistory.getChannel())
                .length(watchHistory.getLength())
                .category(watchHistory.getCategory())
                .status(watchHistory.getStatus())
                .createdAt(watchHistory.getCreatedAt())
                .build();
    }

}
