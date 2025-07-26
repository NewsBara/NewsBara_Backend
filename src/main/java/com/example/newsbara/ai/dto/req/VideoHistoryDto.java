package com.example.newsbara.ai.dto.req;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoHistoryDto {

    @NotNull(message = "Video ID cannot be null")
    private String videoId;

    private String title;  // 필수 아님

    @NotNull(message = "Channel cannot be null")
    private String channel;

    @NotNull(message = "Category cannot be null")
    private String category;
}