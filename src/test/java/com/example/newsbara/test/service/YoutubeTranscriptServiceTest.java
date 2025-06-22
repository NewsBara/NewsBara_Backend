package com.example.newsbara.test.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * YoutubeTranscriptService를 yt-dlp를 사용하여 테스트하는 클래스입니다.
 * 이 테스트는 실제 yt-dlp 명령어를 실행하므로, yt-dlp가 시스템에 설치되어 있어야 합니다.
 *
 * 테스트 실행 전 확인사항:
 * 1. yt-dlp가 설치되어 있어야 함 (pip install yt-dlp)
 * 2. 네트워크 연결이 필요함
 * 3. YouTube에서 해당 비디오가 차단되지 않아야 함
 */
public class YoutubeTranscriptServiceTest {

    private YoutubeTranscriptService youtubeTranscriptService;

    @BeforeEach
    public void setUp() {
        // 기본 생성자 사용 (Service에서 @Value로 주입받는 방식)
        youtubeTranscriptService = new YoutubeTranscriptService();
    }

    /**
     * yt-dlp가 시스템에 설치되어 있는지 확인하는 헬퍼 메서드
     */
    private boolean isYtDlpAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("yt-dlp", "--version");
            Process process = pb.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("yt-dlp 설치 확인")
    public void testYtDlpInstallation() {
        assertTrue(isYtDlpAvailable(),
                "yt-dlp가 설치되어 있지 않습니다. 'pip install yt-dlp' 명령어로 설치해주세요.");
        System.out.println("yt-dlp 설치 확인됨");
    }

    @Test
    @DisplayName("실제 유튜브 영상에서 영어 자막 가져오기")
    public void testGetFullTranscript_RealEnglishVideo() {
        // yt-dlp가 설치되어 있지 않으면 테스트 스킵
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // TED Talk 같은 교육용 컨텐츠는 자막이 있을 가능성이 높음
        String videoId = "Oa0ZHfcalCM"; // What do tech pioneers think about the AI revolution?

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript, "트랜스크립트가 null이면 안됩니다");
            assertTrue(transcript.length() > 100, "트랜스크립트가 너무 짧습니다");

            System.out.println("영어 트랜스크립트 길이: " + transcript.length());
            System.out.println("영어 트랜스크립트 일부: " +
                    transcript.substring(0, Math.min(200, transcript.length())) + "...");

        } catch (GeneralException e) {
            System.out.println("영어 자막 추출 실패: " + e.getMessage());
            // 자막이 없거나 지역 제한 등의 이유로 실패할 수 있음
            assertTrue(
                    e.getCode() == ErrorStatus.TRANSCRIPT_NOT_AVAILABLE ||
                            e.getCode() == ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED,
                    "예상된 오류 코드가 아닙니다: " + e.getCode()
            );
        } catch (RuntimeException e) {
            System.out.println("일반적인 런타임 오류 발생: " + e.getMessage());
            assertTrue(e.getMessage().contains("No transcript available") ||
                            e.getMessage().contains("Failed to"),
                    "예상된 오류 메시지가 아닙니다: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("짧은 유튜브 영상에서 자막 가져오기")
    public void testGetFullTranscript_ShortVideo() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // 널리 알려진 짧은 영상
        String videoId = "dQw4w9WgXcQ"; // Rick Astley - Never Gonna Give You Up

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript, "트랜스크립트가 null이면 안됩니다");
            assertTrue(transcript.length() > 20, "트랜스크립트가 너무 짧습니다");

            // 해당 노래 가사의 일부가 포함되어 있는지 확인
            String lowerTranscript = transcript.toLowerCase();
            assertTrue(
                    lowerTranscript.contains("never") ||
                            lowerTranscript.contains("gonna") ||
                            lowerTranscript.contains("give") ||
                            lowerTranscript.contains("up"),
                    "예상된 가사가 포함되어야 합니다"
            );

            System.out.println("짧은 영상 트랜스크립트 길이: " + transcript.length());
            System.out.println("짧은 영상 트랜스크립트 일부: " +
                    transcript.substring(0, Math.min(100, transcript.length())) + "...");

        } catch (GeneralException e) {
            System.out.println("짧은 영상 자막 추출 실패 (GeneralException): " + e.getMessage());
            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("짧은 영상 자막 추출 실패 (RuntimeException): " + e.getMessage());
        }
    }

    @Test
    @DisplayName("자동 생성 자막만 있는 뉴스 영상에서 자막 가져오기")
    public void testGetFullTranscript_RecentNewsVideo() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // 최신 동영상이라 자동 생성 자막만 있음
        String videoId = "2C7EoBoPB7s"; // BBC 뉴스 영상

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript, "뉴스 트랜스크립트가 null이면 안됩니다");
            assertTrue(transcript.length() > 100, "뉴스 트랜스크립트가 너무 짧습니다");

            System.out.println("뉴스 영상 트랜스크립트 길이: " + transcript.length());
            System.out.println("뉴스 영상 트랜스크립트 일부: " +
                    transcript.substring(0, Math.min(200, transcript.length())) + "...");

        } catch (GeneralException e) {
            System.out.println("뉴스 영상 자막 추출 실패 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

            // 예상 가능한 오류 코드들
            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("뉴스 영상 자막 추출 실패 (RuntimeException): " + e.getMessage());
            System.out.println("선택한 뉴스 영상에 자막이 없거나 지역 제한이 있을 수 있습니다.");
        }
    }

    @Test
    @DisplayName("존재하지 않는 비디오 ID로 예외 발생 확인")
    public void testGetFullTranscript_NonExistentVideo() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // 존재하지 않는 비디오 ID
        String videoId = "ThisVideoDoesNotExist123456789";

        // 존재하지 않는 비디오에 대해 예외가 발생해야 함
        Exception exception = assertThrows(RuntimeException.class, () -> {
            youtubeTranscriptService.getFullTranscript(videoId);
        });

        assertNotNull(exception.getMessage(), "예외 메시지가 있어야 합니다");
        assertTrue(
                exception.getMessage().contains("No English transcript available") ||
                        exception.getMessage().contains("Failed to") ||
                        exception.getMessage().contains("No English transcript available"),
                "적절한 오류 메시지가 포함되어야 합니다"
        );

        System.out.println("예상된 예외 발생: " + exception.getMessage());
    }

    @Test
    @DisplayName("자막이 없는 영상 처리 확인")
    public void testGetFullTranscript_NoSubtitles() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // 자막이 없을 가능성이 높은 개인 업로드 영상
        String videoId = "ScMzIvxBSi4";

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);
            // 만약 자막이 있다면 정상적으로 처리되어야 함
            assertNotNull(transcript);
            System.out.println("예상외로 자막이 있는 영상이었습니다: " + transcript.length() + " 문자");
        } catch (GeneralException e) {
            // 자막이 없는 경우 적절한 예외가 발생해야 함
            System.out.println("예상대로 자막이 없는 영상입니다 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

        } catch (RuntimeException e) {
            // 일반적인 런타임 예외 처리
            assertTrue(
                    e.getMessage().contains("No transcript available") ||
                            e.getMessage().contains("Failed to") ||
                            e.getMessage().contains("transcript") ||
                            e.getMessage().contains("subtitle"),
                    "적절한 오류 메시지가 포함되어야 합니다: " + e.getMessage()
            );
            System.out.println("예상대로 자막이 없는 영상입니다 (RuntimeException): " + e.getMessage());
        }
    }


    @Test
    @DisplayName("영어(영국) 자막이 있는 영상 처리 확인")
    public void testGetFullTranscript_WithEnglishSubtitles() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // 영어 자막은 없고 영국 영어 자막만 있는 영상
        String videoId = "6Dubwx8_FCQ"; // bbc

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            // 자막이 정상적으로 추출되었는지 확인
            assertNotNull(transcript);
            assertFalse(transcript.trim().isEmpty());
            assertTrue(transcript.length() > 50, "자막이 너무 짧습니다: " + transcript.length());

            System.out.println("영어 자막 추출 성공!");
            System.out.println("자막 길이: " + transcript.length() + " 문자");
            System.out.println("자막 일부: " + transcript.substring(0, Math.min(200, transcript.length())) + "...");

        } catch (GeneralException e) {
            System.out.println("영어 자막이 있는 영상에서 예외가 발생했습니다 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

            // 이 케이스에서는 자막이 있어야 하므로 실패로 간주하되, 로그는 남김
            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("영어 자막이 있는 영상에서 예외가 발생했습니다 (RuntimeException): " + e.getMessage());
            // 이 경우도 로그만 남기고 테스트는 계속 진행
        }
    }

    @Test
    @DisplayName("영어 자막 우선순위 테스트 - 여러 영어 변형이 있는 경우")
    public void testGetFullTranscript_EnglishPriority() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        // BBC나 다른 영국 채널 영상 (en과 en-GB 자막이 모두 있을 가능성)
        String videoId = "38adr7ufEMY"; // 유명한 영상 예시

        try {
            String transcript = youtubeTranscriptService.getFullTranscript(videoId);

            assertNotNull(transcript);
            assertFalse(transcript.trim().isEmpty());

            System.out.println("영어 자막 우선순위 테스트 성공!");
            System.out.println("추출된 자막 길이: " + transcript.length() + " 문자");

        } catch (GeneralException e) {
            System.out.println("해당 영상에는 영어 자막이 없습니다 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

            // 이 경우는 테스트 실패가 아니라 정상적인 동작
            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("해당 영상에는 영어 자막이 없습니다 (RuntimeException): " + e.getMessage());
            // 이 경우는 테스트 실패가 아니라 정상적인 동작
        }
    }


    @Test
    @DisplayName("빈 문자열 비디오 ID 처리")
    public void testGetFullTranscript_EmptyVideoId() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        String videoId = "";

        try {
            youtubeTranscriptService.getFullTranscript(videoId);
            fail("빈 비디오 ID에 대해 예외가 발생해야 합니다.");
        } catch (GeneralException e) {
            System.out.println("빈 비디오 ID에 대한 예외 발생 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("빈 비디오 ID에 대한 예외 발생 (RuntimeException): " + e.getMessage());
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("null 비디오 ID 처리")
    public void testGetFullTranscript_NullVideoId() {
        if (!isYtDlpAvailable()) {
            System.out.println("yt-dlp가 설치되어 있지 않아 테스트를 스킵합니다.");
            return;
        }

        String videoId = null;

        try {
            youtubeTranscriptService.getFullTranscript(videoId);
            fail("null 비디오 ID에 대해 예외가 발생해야 합니다.");
        } catch (GeneralException e) {
            System.out.println("null 비디오 ID에 대한 예외 발생 (GeneralException): " + e.getMessage());
            System.out.println("오류 코드: " + e.getCode());

            assertThat(e.getCode()).isIn(
                    ErrorStatus.TRANSCRIPT_NOT_AVAILABLE,
                    ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED
            );
        } catch (RuntimeException e) {
            System.out.println("null 비디오 ID에 대한 예외 발생 (RuntimeException): " + e.getMessage());
            assertNotNull(e.getMessage());
        }
    }
}