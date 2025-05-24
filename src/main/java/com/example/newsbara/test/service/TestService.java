package com.example.newsbara.test.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.score.domain.Score;
import com.example.newsbara.score.domain.enums.TestType;
import com.example.newsbara.score.repository.ScoreRepository;
import com.example.newsbara.test.dto.res.TestResDto;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@Service
public class TestService {

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


        // 최신 Score를 가져오되 없으면 null
        Score userScore = scoreRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);


        // Get transcript
        String transcript = transcriptService.getFullTranscript(videoId, "en");

        // Generate test using GPT
        Map<String, String> testContent = gptService.generateTest(transcript, userScore);

        // Return the response without saving to database
        return TestResDto.TestResponse.builder()
                .videoId(videoId)
                .summary(testContent.get("summary"))
                .answer(testContent.get("answer"))
                .explanation(testContent.get("explanation"))
                .build();
    }
}
