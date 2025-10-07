package com.example.newsbara.test.service;

import org.springframework.stereotype.Service;
import io.github.thoroldvix.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YoutubeTranscriptService {

    private final YoutubeTranscriptApi transcriptApi;
    // 영어 자막 언어 코드 우선순위 (일반 영어 -> 기타 영어 변형)
    private static final List<String> ENGLISH_LANGUAGE_CODES = Arrays.asList(
            "en",           // 영어 (일반)
            "en-en",
            "en-GB",        // 영어 (영국)
            "en-US",        // 영어 (미국)
            "en-AU",        // 영어 (호주)
            "en-CA"        // 영어 (캐나다)
    );

    public YoutubeTranscriptService() {
        this.transcriptApi = TranscriptApiFactory.createDefault();
    }

    /**
     * 유튜브 동영상의 자막을 하나의 완전한 스크립트 문자열로 반환합니다.
     * 우선순위에 따라 영어 자막을 시도합니다.
     */
    public String getFullTranscript(String videoId) {
        TranscriptContent content = null;
        TranscriptRetrievalException lastException = null;

        // 우선순위대로 자막 시도
        for (String languageCode : ENGLISH_LANGUAGE_CODES) {
            try {
                content = transcriptApi.getTranscript(videoId, languageCode);
                // 성공하면 바로 반환
                break;
            } catch (TranscriptRetrievalException e) {
                // 실패하면 다음 언어 코드 시도
                lastException = e;
                continue;
            }
        }

        // 모든 언어 코드를 시도했지만 실패한 경우
        if (content == null) {
            throw new RuntimeException("No English transcript available for video: " + videoId, lastException);
        }

        return content.getContent().stream()
                .map(TranscriptContent.Fragment::getText)
                .collect(Collectors.joining(" "));
    }
}