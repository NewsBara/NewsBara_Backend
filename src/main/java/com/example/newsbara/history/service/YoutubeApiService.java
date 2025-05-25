package com.example.newsbara.history.service;

import com.example.newsbara.history.dto.res.YoutubeApiResponse;
import com.example.newsbara.history.dto.res.YoutubeCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class YoutubeApiService {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    private final WebClient webClient = WebClient.create("https://www.googleapis.com/youtube/v3");

    public YoutubeVideoInfo getVideoInfo(String videoId) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/videos")
                            .queryParam("part", "snippet,contentDetails")
                            .queryParam("id", videoId)
                            .queryParam("key", youtubeApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(YoutubeApiResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .map(response -> {
                        if (response.getItems().isEmpty()) {
                            throw new RuntimeException("Video not found");
                        }
                        YoutubeApiResponse.Item item = response.getItems().get(0);
                        return YoutubeVideoInfo.builder()
                                .title(item.getSnippet().getTitle())
                                .thumbnail(item.getSnippet().getThumbnails().getDefault().getUrl())
                                .channel(item.getSnippet().getChannelTitle())
                                .length(item.getContentDetails().getDuration()) // ISO 8601 형식
                                .category(item.getSnippet().getCategoryId()) // 카테고리 이름은 별도 API 필요
                                .build();
                    })
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("YouTube API Error: " + e.getStatusCode());
        }
    }

    public String getCategoryName(String categoryId) {
        try {
            YoutubeCategoryResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/videoCategories")
                            .queryParam("part", "snippet")
                            .queryParam("id", categoryId)
                            .queryParam("regionCode", "KR")
                            .queryParam("key", youtubeApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(YoutubeCategoryResponse.class)
                    .block();

            if (response != null && !response.getItems().isEmpty()) {
                return response.getItems().get(0).getSnippet().getTitle();
            } else {
                return "기타";
            }
        } catch (Exception e) {
            return "기타";
        }
    }

}
