package com.example.newsbara.test.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.score.domain.Score;
import com.example.newsbara.score.domain.enums.TestType;
import com.example.newsbara.score.repository.ScoreRepository;
import com.example.newsbara.test.dto.res.TestResDto;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

    private final YoutubeTranscriptService transcriptService;
    private final GptService gptService;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    public TestService(YoutubeTranscriptService transcriptService,
                       GptService gptService,
                       UserRepository userRepository,
                       ScoreRepository scoreRepository) {
        this.transcriptService = transcriptService;
        this.gptService = gptService;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    public TestResDto.TestResponse generateTest(Principal principal, String videoId) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // null을 허용
        List<Score> userScore = scoreRepository.findAllByUser(user);

        userScore = userScore.isEmpty() ? null : userScore;

        try {
            logger.info("Attempting to generate test for video: {} by user: {}", videoId, user.getEmail());

            // Get English transcript with priority system (en -> en-US -> en-GB -> etc.)
            String transcript = transcriptService.getFullTranscript(videoId);

            logger.info("Successfully retrieved transcript for video: {}", videoId);

            // Generate test using GPT
            Map<String, String> testContent = gptService.generateTest(transcript, userScore);

            logger.info("Successfully generated test content for video: {}", videoId);

            // Return the response without saving to database
            return TestResDto.TestResponse.builder()
                    .videoId(videoId)
                    .summary(testContent.get("summary"))
                    .answer(testContent.get("answer"))
                    .explanation(testContent.get("explanation"))
                    .build();

        } catch (RuntimeException e) {
            logger.error("Failed to generate test for video: {} by user: {}", videoId, user.getEmail(), e);

            // 영어 자막이 없는 경우 더 구체적인 에러 메시지
            if (e.getMessage().contains("No English")) {
                throw new GeneralException(ErrorStatus.TRANSCRIPT_NOT_AVAILABLE);
            }

            // 기타 transcript 관련 오류
            if (e.getMessage().contains("transcript") || e.getMessage().contains("subtitle")) {
                throw new GeneralException(ErrorStatus.TRANSCRIPT_EXTRACTION_FAILED);
            }

            // 일반적인 오류
            throw new GeneralException(ErrorStatus.TEST_GENERATION_FAILED);
        }
    }
}