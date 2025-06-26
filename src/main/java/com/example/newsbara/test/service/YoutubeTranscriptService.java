package com.example.newsbara.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class YoutubeTranscriptService {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeTranscriptService.class);
    private final ObjectMapper objectMapper;
    private final String tempDir;
    private final String cookiesFilePath;

    // 영어 자막 언어 코드 우선순위 (일반 영어 -> 기타 영어 변형)
    private static final List<String> ENGLISH_LANGUAGE_CODES = Arrays.asList(
            "en",           // 영어 (일반)
            "en-en",
            "en-GB",        // 영어 (영국)
            "en-US",        // 영어 (미국)
            "en-AU",        // 영어 (호주)
            "en-CA"        // 영어 (캐나다)
    );

    // 기본 생성자 (테스트용)
    public YoutubeTranscriptService() {
        this.objectMapper = new ObjectMapper();
        this.tempDir = System.getProperty("java.io.tmpdir");
        this.cookiesFilePath = null;
    }

    // Spring에서 사용하는 생성자
    public YoutubeTranscriptService(
            @Value("${app.temp.dir:#{systemProperties['java.io.tmpdir']}}") String tempDir,
            @Value("${youtube.cookies.file.path:}") String cookiesFilePath) {
        this.objectMapper = new ObjectMapper();
        this.tempDir = tempDir;

        logger.info("⭐️ @Value 설정 주입 확인 - tempDir: {}", tempDir);
        logger.info("⭐️ @Value 설정 주입 확인 - cookiesFilePath (raw): {}", cookiesFilePath);


        // 쿠키 파일 경로 정규화 및 절대 경로 처리
        this.cookiesFilePath = normalizeCookiesPath(cookiesFilePath);

        // 생성자에서 쿠키 파일 상태 확인 및 로깅
        logCookieFileStatus();
    }

    private String normalizeCookiesPath(String cookiesFilePath) {
        if (cookiesFilePath == null || cookiesFilePath.trim().isEmpty()) {
            logger.warn("⭐️ normalizeCookiesPath: 빈 값 또는 null 입력됨");
            return null;
        }

        String normalizedPath = cookiesFilePath.trim();

        // 절대 경로가 아닌 경우 현재 작업 디렉토리 기준으로 처리
        if (!normalizedPath.startsWith("/") && !normalizedPath.matches("^[a-zA-Z]:.*")) {
            String workingDir = System.getProperty("user.dir");
            normalizedPath = Paths.get(workingDir, normalizedPath).toString();
            logger.info("⭐️ 상대 경로로 인식되어 절대 경로로 변환됨: {}", normalizedPath);
        } else {
            logger.info("⭐️ 절대 경로로 사용됨: {}", normalizedPath);
        }

        return normalizedPath;
    }

    private void logCookieFileStatus() {
        logger.info("=== Cookie File Status Check ===");
        logger.info("Raw cookies file path from config: {}",
                System.getProperty("youtube.cookies.file.path", "NOT_SET"));
        logger.info("Processed cookies file path: {}", cookiesFilePath);
        logger.info("Current working directory: {}", System.getProperty("user.dir"));

        if (cookiesFilePath != null) {
            Path cookiesPath = Paths.get(cookiesFilePath);
            logger.info("Checking path: {}", cookiesPath.toAbsolutePath());

            if (Files.exists(cookiesPath)) {
                try {
                    long fileSize = Files.size(cookiesPath);
                    boolean isReadable = Files.isReadable(cookiesPath);
                    logger.info("✓ Cookies file found: {} (size: {} bytes, readable: {})",
                            cookiesFilePath, fileSize, isReadable);

                    // 파일 내용 첫 줄 확인 (디버깅용)
                    try {
                        String firstLine = Files.lines(cookiesPath).findFirst().orElse("");
                        logger.info("First line of cookies file: {}", firstLine);
                    } catch (Exception e) {
                        logger.warn("Cannot read first line of cookies file", e);
                    }
                } catch (Exception e) {
                    logger.warn("✗ Cookies file exists but cannot read properties: {}", cookiesFilePath, e);
                }
            } else {
                logger.warn("✗ Cookies file path configured but file not found: {}", cookiesFilePath);

                // 부모 디렉토리 존재 여부 확인
                Path parentDir = cookiesPath.getParent();
                if (parentDir != null) {
                    logger.info("Parent directory exists: {}", Files.exists(parentDir));
                    if (Files.exists(parentDir)) {
                        try {
                            logger.info("Files in parent directory: {}",
                                    Arrays.toString(Files.list(parentDir).map(Path::getFileName).toArray()));
                        } catch (Exception e) {
                            logger.warn("Cannot list parent directory", e);
                        }
                    }
                }
            }
        } else {
            logger.info("No cookies file configured. YouTube may require authentication for some videos.");
        }
        logger.info("=== End Cookie File Status Check ===");
    }

    // VTT 자막에서 텍스트만 추출하는 정규식
    private static final Pattern VTT_TEXT_PATTERN = Pattern.compile("^(?!WEBVTT|NOTE|\\d{2}:|-->)[^\\n]*$", Pattern.MULTILINE);
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");

    /**
     * 유튜브 동영상의 영어 자막을 하나의 완전한 스크립트 문자열로 반환합니다.
     * 우선순위: en -> en-US -> en-GB -> 기타 영어 변형
     */
    public String getFullTranscript(String videoId) {
        String tempFilePath = null;
        Path tempDirPath = null;

        try {
            // 임시 디렉토리 생성
            tempDirPath = Paths.get(tempDir, "youtube-transcripts");
            Files.createDirectories(tempDirPath);

            // 영어 자막 다운로드 시도 (우선순위 순서대로)
            String actualLanguageCode = null;
            for (String langCode : ENGLISH_LANGUAGE_CODES) {
                try {
                    logger.info("Trying to download subtitles with language code: {}", langCode);
                    tempFilePath = downloadSubtitles(videoId, langCode, tempDirPath.toString());
                    actualLanguageCode = langCode;
                    logger.info("Successfully downloaded subtitles with language code: {}", langCode);
                    break;
                } catch (Exception e) {
                    logger.debug("Failed to download subtitles with language code: {} - {}", langCode, e.getMessage());
                    // 다음 언어 코드로 시도
                }
            }

            if (tempFilePath == null || actualLanguageCode == null) {
                throw new RuntimeException("No English subtitles available for video: " + videoId);
            }

            // 자막 파일 읽기 및 파싱
            String transcript = parseSubtitleFile(tempFilePath);
            logger.info("Successfully extracted transcript using language code: {}", actualLanguageCode);
            return transcript;

        } catch (Exception e) {
            logger.error("Error extracting English transcript for video: {}", videoId, e);
            throw new RuntimeException("No English transcript available for video: " + videoId, e);
        } finally {
            // 임시 파일 정리
            cleanupTempFiles(tempDirPath, videoId);
        }
    }

    /**
     * 기존 메서드와의 호환성을 위한 오버로드된 메서드
     */
    public String getFullTranscript(String videoId, String languageCode) {
        // 영어 관련 언어 코드가 아닌 경우 예외 처리
        if (!isEnglishLanguageCode(languageCode)) {
            throw new RuntimeException("Only English subtitles are supported. Requested language: " + languageCode);
        }

        // 영어 언어 코드인 경우 우선순위 기반 다운로드 실행
        return getFullTranscript(videoId);
    }

    private boolean isEnglishLanguageCode(String languageCode) {
        return ENGLISH_LANGUAGE_CODES.contains(languageCode) ||
                languageCode.toLowerCase().startsWith("en");
    }

    private String downloadSubtitles(String videoId, String languageCode, String outputDir) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("yt-dlp");

        // 쿠키 파일 처리 개선
        boolean usingCookies = false;
        if (cookiesFilePath != null) {
            Path cookiesPath = Paths.get(cookiesFilePath);
            logger.info("⭐️ 쿠키 파일 경로 확인 중: {}", cookiesPath.toAbsolutePath());

            if (Files.exists(cookiesPath) && Files.isReadable(cookiesPath)) {
                command.add("--cookies");
                command.add(cookiesFilePath);
                usingCookies = true;
                logger.info("⭐️ ✓ 쿠키 파일 사용됨: {}", cookiesFilePath);
            } else {
                logger.warn("⭐️ ✗ 쿠키 파일 설정됨 but 접근 불가: {} (exists: {}, readable: {})",
                        cookiesFilePath, Files.exists(cookiesPath), Files.isReadable(cookiesPath));
            }
        } else {
            logger.warn("⭐️ 쿠키 파일 경로가 null. yt-dlp 인증 없이 실행됨");
        }

        if (!usingCookies) {
            logger.warn("No valid cookies file available. This may cause authentication issues with YouTube.");
        }

        command.add("--write-subs");
        command.add("--write-auto-subs");
        command.add("--sub-langs");
        command.add(languageCode);
        command.add("--skip-download");
        command.add("--sub-format");
        command.add("vtt");
        command.add("-o");
        command.add(outputDir + "/%(id)s.%(ext)s");

        // User-Agent 추가로 봇 탐지 우회 시도
        command.add("--user-agent");
        command.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // 요청 간격 설정 (너무 빠른 요청 방지)
        command.add("--sleep-interval");
        command.add("1");
        command.add("--max-sleep-interval");
        command.add("3");

        // 추가 옵션들 - 안정성 향상
        command.add("--no-check-certificates");  // SSL 인증서 검사 비활성화
        command.add("--prefer-insecure");        // HTTP 선호 (선택사항)

        command.add("https://www.youtube.com/watch?v=" + videoId);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(outputDir));

        logger.debug("Executing command: {}", String.join(" ", command));

        Process process = processBuilder.start();

        // 에러 출력 캡처
        StringBuilder errorOutput = new StringBuilder();
        StringBuilder standardOutput = new StringBuilder();

        try (BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
             BufferedReader outputReader = new BufferedReader(
                     new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            while ((line = outputReader.readLine()) != null) {
                standardOutput.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(90, TimeUnit.SECONDS); // 타임아웃 증가
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("yt-dlp process timed out");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            logger.debug("yt-dlp failed with exit code: {}, error: {}, output: {}",
                    exitCode, errorOutput, standardOutput);
            throw new RuntimeException("Failed to download subtitles for language: " + languageCode +
                    ". Error: " + errorOutput.toString());
        }

        // 생성된 자막 파일 찾기
        String expectedFileName = videoId + "." + languageCode + ".vtt";
        Path subtitleFile = Paths.get(outputDir, expectedFileName);

        if (!Files.exists(subtitleFile)) {
            // 자동 생성 자막 파일명 시도
            expectedFileName = videoId + "." + languageCode + ".auto.vtt";
            subtitleFile = Paths.get(outputDir, expectedFileName);
        }

        if (!Files.exists(subtitleFile)) {
            throw new RuntimeException("Subtitle file not found after download for language: " + languageCode);
        }

        logger.debug("Subtitle file created: {}", subtitleFile);
        return subtitleFile.toString();
    }

    private void cleanupTempFiles(Path tempDirPath, String videoId) {
        if (tempDirPath != null && Files.exists(tempDirPath)) {
            try {
                // 해당 비디오 ID로 시작하는 모든 임시 파일 삭제
                Files.list(tempDirPath)
                        .filter(path -> path.getFileName().toString().startsWith(videoId))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                logger.debug("Deleted temp file: {}", path);
                            } catch (IOException e) {
                                logger.warn("Failed to delete temp file: {}", path, e);
                            }
                        });
            } catch (IOException e) {
                logger.warn("Failed to cleanup temp files in directory: {}", tempDirPath, e);
            }
        }
    }

    private String parseSubtitleFile(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));

        // VTT 형식 파싱
        if (filePath.endsWith(".vtt")) {
            return parseVttContent(content);
        }

        // SRT 형식 파싱 (필요한 경우)
        if (filePath.endsWith(".srt")) {
            return parseSrtContent(content);
        }

        // 기본적으로 VTT로 처리
        return parseVttContent(content);
    }

    private String parseVttContent(String content) {
        StringBuilder transcript = new StringBuilder();
        String[] lines = content.split("\n");

        boolean isTextSection = false;
        for (String line : lines) {
            line = line.trim();

            // 빈 줄이면 섹션 구분
            if (line.isEmpty()) {
                isTextSection = false;
                continue;
            }

            // WEBVTT 헤더나 NOTE 스킵
            if (line.startsWith("WEBVTT") || line.startsWith("NOTE")) {
                continue;
            }

            // 시간 코드 라인 확인
            if (TIME_PATTERN.matcher(line).find()) {
                isTextSection = true;
                continue;
            }

            // 텍스트 라인
            if (isTextSection) {
                // HTML 태그 제거
                String cleanText = line.replaceAll("<[^>]*>", "");
                if (!cleanText.isEmpty()) {
                    transcript.append(cleanText).append(" ");
                }
            }
        }

        return transcript.toString().trim();
    }

    private String parseSrtContent(String content) {
        StringBuilder transcript = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();

            // 빈 줄이나 숫자만 있는 라인(자막 번호) 스킵
            if (line.isEmpty() || line.matches("^\\d+$")) {
                continue;
            }

            // 시간 코드 라인 스킵
            if (line.contains("-->")) {
                continue;
            }

            // 텍스트 라인 추가
            transcript.append(line).append(" ");
        }

        return transcript.toString().trim();
    }
}