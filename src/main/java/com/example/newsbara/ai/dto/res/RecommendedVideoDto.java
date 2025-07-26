package com.example.newsbara.ai.dto.res;

import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
}