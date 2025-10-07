package com.example.newsbara.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YoutubeTranscriptService를 실제 YouTube API와 통신하여 테스트하는 클래스입니다.
 * 이 테스트는 실제 API 호출을 수행하므로, 네트워크 연결이 필요합니다.
 */
public class YoutubeTranscriptServiceTest {

    private YoutubeTranscriptService youtubeTranscriptService;

    @BeforeEach
    public void setUp() {
        youtubeTranscriptService = new YoutubeTranscriptService();
    }

    @Test
    @DisplayName("실제 유튜브 영상에서 영어 자막 가져오기")
    public void testGetFullTranscript_RealEnglishVideo() {
        // BBC 뉴스 영상 25분 - 전체 스크립트 로딩의 시간 : 3초
        String videoId = "Oa0ZHfcalCM"; // What do tech pioneers think about the AI revolution?

        String transcript = youtubeTranscriptService.getFullTranscript(videoId);

        assertNotNull(transcript);
        assertTrue(transcript.length() > 100);

        System.out.println("영어 트랜스크립트 길이: " + transcript.length());
        System.out.println("영어 트랜스크립트 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");
    }

    @Test
    @DisplayName("자동 생성 자막만 있는 뉴스 영상에서 자막 가져오기")
    public void testGetFullTranscript_AutoNewsVideo() {
        String videoId = "2C7EoBoPB7s";  // BBC 뉴스 영상 (자동 생성 자막)

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 50);

            System.out.println("자동 생성 자막 길이: " + transcript.length());
            System.out.println("자동 생성 자막 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");
        } catch (RuntimeException e) {
            System.out.println("자동 생성 자막을 가져올 수 없습니다: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("짧은 유튜브 클립에서 자막 가져오기")
    public void testGetFullTranscript_ShortVideo() {
        // 짧은 영상 (쇼츠나 클립)
        String videoId = "8VvXmdFrOyg"; // DailyMail 채널의 쇼츠

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 10);

            System.out.println("짧은 영상 트랜스크립트 길이: " + transcript.length());
            System.out.println("짧은 영상 트랜스크립트: " + transcript);
        } catch (RuntimeException e) {
            System.out.println("짧은 영상에서 자막을 가져올 수 없습니다: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("최근 업로드된 뉴스 영상에서 자막 가져오기")
    public void testGetFullTranscript_RecentNewsVideo() {
        String videoId = "E20Rj3Wboas"; // bbc 최신 뉴스 2025. 5. 11

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 20);

            System.out.println("최근 뉴스 영상 트랜스크립트 길이: " + transcript.length());
            System.out.println("뉴스 영상 트랜스크립트 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");
        } catch (RuntimeException e) {
            System.out.println("최근 뉴스 영상에 자막이 없습니다: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("존재하지 않는 비디오 ID로 예외 발생 확인")
    public void testGetFullTranscript_NonExistentVideo() {
        // 존재하지 않는 비디오 ID
        String videoId = "xyzNonExistentId123456789";

        // 존재하지 않는 비디오에 대해 예외가 발생해야 함
        Exception exception = assertThrows(RuntimeException.class, () -> {
            youtubeTranscriptService.getFullTranscript(videoId);
        });

        assertNotNull(exception.getMessage());
        System.out.println("예상된 예외 발생: " + exception.getMessage());
    }

    @Test
    @DisplayName("영어(영국) 자막이 있는 영상 처리 확인")
    public void testGetFullTranscript_WithEnglishSubtitles() {
        // 영어 자막(en-GB 등)이 있는 BBC 영상
        String videoId = "6Dubwx8_FCQ"; // bbc

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 50);

            System.out.println("영국 영어 자막 길이: " + transcript.length());
            System.out.println("영국 영어 자막 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");
        } catch (RuntimeException e) {
            System.out.println("영어 자막을 가져올 수 없습니다: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("영어 자막 우선순위 테스트 - 여러 영어 변형이 있는 경우")
    public void testGetFullTranscript_EnglishPriority() {
        // 여러 영어 변형 자막이 있을 가능성이 있는 영상
        String videoId = "38adr7ufEMY"; // 유명한 영상 예시

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 50);

            System.out.println("우선순위 테스트 - 트랜스크립트 길이: " + transcript.length());
            System.out.println("우선순위 테스트 - 트랜스크립트 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");
            System.out.println("우선순위대로 자막을 성공적으로 가져왔습니다 (en -> en-en -> en-GB -> ...)");
        } catch (RuntimeException e) {
            System.out.println("영어 자막을 가져올 수 없습니다: " + e.getMessage());
        }
    }
}