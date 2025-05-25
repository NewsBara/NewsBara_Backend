package com.example.newsbara.history.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YoutubeVideoInfo {
    private String title;
    private String thumbnail;
    private String channel;
    private String length;
    private String category;
}
