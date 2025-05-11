package com.example.newsbara.test.service;

import io.github.thoroldvix.internal.TranscriptApiFactory;
import org.springframework.stereotype.Service;
import io.github.thoroldvix.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YoutubeTranscriptService {

    private final YoutubeTranscriptApi transcriptApi;

    public YoutubeTranscriptService() {
        this.transcriptApi = TranscriptApiFactory.createDefault();
    }

    /**
     * 유튜브 동영상의 자막을 하나의 완전한 스크립트 문자열로 반환합니다.
     */
    public String getFullTranscript(String videoId, String languageCode) {
        try {
            TranscriptContent content = transcriptApi.getTranscript(videoId, languageCode);
            var segments = content.getContent(); // 여기에 마우스를 올려 반환 타입 확인

            return content.getContent().stream()
                    .map(TranscriptContent.Fragment::getText)
                    .collect(Collectors.joining(" "));

        } catch (TranscriptRetrievalException e) {
            throw new RuntimeException("No transcript available", e);
        }
    }
}