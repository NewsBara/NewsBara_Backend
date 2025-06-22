package com.example.newsbara.history.dto.req;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.domain.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import static com.example.newsbara.history.domain.enums.Status.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class HistoryReqDto {
    private String videoId;
    private String title;
    private String thumbnail;
    private String channel;

    @Schema(description = "비디오 길이 (HH:MM:SS 형식)", example = "00:03:33", pattern = "^\\d{2}:\\d{2}:\\d{2}$")
    private String length;

    @Schema(description = "카테고리", example = "Music")
    private String category;

    @Schema(description = "시청 상태", allowableValues = {"WATCHED", "SHADOWING", "TEST", "WORD", "COMPLETED"})
    private Status status;

    public WatchHistory toEntity() {
        return WatchHistory.builder()
                .videoId(this.videoId)
                .title(this.title)
                .thumbnail(this.thumbnail)
                .channel(this.channel)
                .length(this.length)    // HH:MM:ss 형식으로 준다고 가정
                .category(this.category)   // 실제 카테고리명
                .status(this.status)
                .build();
    }

}
