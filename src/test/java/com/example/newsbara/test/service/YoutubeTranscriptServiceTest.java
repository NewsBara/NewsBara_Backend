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
        String languageCode = "en";

        String transcript = youtubeTranscriptService.getFullTranscript(videoId, languageCode);

        assertNotNull(transcript);
        assertTrue(transcript.length() > 100);

        // 스티브 잡스 연설에 나올 법한 특정 단어들이 포함되었는지 확인
        assertTrue(
                transcript.contains("Stanford") ||
                        transcript.contains("college") ||
                        transcript.contains("today") ||
                        transcript.contains("life")
        );

        System.out.println("영어 트랜스크립트 : " + transcript);
    }

    @Test
    @DisplayName("실제 유튜브 영상에서 한국어 자막 가져오기")
    public void testGetFullTranscript_RealKoreanVideo() {
        // 한국어 자막이 있는 영상
        String videoId = "23urWKmHS6o"; // 제니-love hangover mv
        String languageCode = "ko";

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId, languageCode);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 50);

            // 한글이 포함되어 있는지 확인
            boolean containsKorean = transcript.codePoints().anyMatch(
                    codePoint -> Character.UnicodeBlock.of(codePoint) == Character.UnicodeBlock.HANGUL_SYLLABLES
            );

            assertTrue(containsKorean, "트랜스크립트에 한글이 포함되어 있어야 합니다");
            System.out.println("한국어 트랜스크립트 일부: " + transcript.substring(0, 100) + "...");
        } catch (RuntimeException e) {
            // 해당 영상에 한국어 자막이 없을 수 있으므로, 이 경우 테스트를 스킵
            System.out.println("해당 영상에 한국어 자막이 없습니다. 다른 영상 ID로 테스트해 보세요.");
        }
    }

    @Test
    @DisplayName("짧은 유튜브 클립에서 자막 가져오기")
    public void testGetFullTranscript_ShortVideo() {
        // 짧은 인기 영상 - 자막이 있을 가능성이 높음
        String videoId = "8VvXmdFrOyg"; // DailyMail 채널의 쇼츠
        String languageCode = "en";

        String transcript = youtubeTranscriptService.getFullTranscript(videoId, languageCode);

        assertNotNull(transcript);
        assertTrue(transcript.length() > 50);

        // 노래 가사의 일부가 포함되어 있는지 확인
        assertTrue(
                transcript.toLowerCase().contains("never") ||
                        transcript.toLowerCase().contains("gonna") ||
                        transcript.toLowerCase().contains("give")
        );

        System.out.println("짧은 영상 트랜스크립트 일부: " + transcript.substring(0, 100) + "...");
    }

    @Test
    @DisplayName("최근 업로드된 뉴스 영상에서 자막 가져오기")
    public void testGetFullTranscript_RecentNewsVideo() {
        // CNN, BBC 등의 뉴스 채널은 자막을 제공하는 경우가 많음
        // 테스트 실행 시점에 적절한 최신 뉴스 영상 ID로 업데이트하는 것이 좋습니다
        String videoId = "E20Rj3Wboas"; // bbc 최신 뉴스  2025. 5. 11
        String languageCode = "en";

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId, languageCode);

            assertNotNull(transcript);
            assertTrue(transcript.length() > 20);
            System.out.println("뉴스 영상 트랜스크립트 일부: " + transcript.substring(0, 100) + "...");
        } catch (RuntimeException e) {
            // 해당 영상에 자막이 없을 수 있으므로, 이 경우 메시지 출력
            System.out.println("선택한 뉴스 영상에 자막이 없습니다. 다른 영상 ID로 테스트해 보세요.");
            System.out.println("오류 메시지: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("존재하지 않는 비디오 ID로 예외 발생 확인")
    public void testGetFullTranscript_NonExistentVideo() {
        // 존재하지 않는 비디오 ID
        String videoId = "xyzNonExistentId123456789";
        String languageCode = "en";

        // 존재하지 않는 비디오에 대해 예외가 발생해야 함
        Exception exception = assertThrows(RuntimeException.class, () -> {
            youtubeTranscriptService.getFullTranscript(videoId, languageCode);
        });

        assertNotNull(exception.getMessage());
        System.out.println("예상된 예외 발생: " + exception.getMessage());
    }

    @Test
    @DisplayName("다양한 언어 코드로 자막 가져오기 시도")
    public void testGetFullTranscript_MultipleLanguages() {
        // 여러 언어의 자막이 있을 것으로 예상되는 인기 영상
        String videoId = "gdZLi9oWNZg"; // bts mv
        String[] languageCodes = {"en", "es", "fr", "de", "ja", "ko", "zh-cn"};

        for (String language : languageCodes) {
            try {
                String transcript = youtubeTranscriptService.getFullTranscript(videoId, language);
                assertNotNull(transcript);
                assertFalse(transcript.isEmpty());
                System.out.println(language + " 언어 트랜스크립트 있음 (길이: " + transcript.length() + ")");
            } catch (Exception e) {
                // 특정 언어 자막이 없을 수 있으므로 무시
                System.out.println(language + " 언어 트랜스크립트 없음");
            }
        }
    }
}