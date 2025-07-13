package com.example.newsbara.test.service;

import com.example.newsbara.score.domain.Score;
import com.example.newsbara.test.util.GptPromptBuilder;
import com.example.newsbara.test.util.GptResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GptService {

    private static final Logger logger = LoggerFactory.getLogger(GptService.class);

    private final OpenAiChatModel openAiChatModel;

    @Autowired
    public GptService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    public Map<String, String> generateTest(String transcript, List<Score> userScore) {
        try {
            String promptContent = GptPromptBuilder.buildPrompt(transcript, userScore);
            // 사용자 점수 정보 로깅
            if (userScore != null && !userScore.isEmpty()) {
                logger.info("=== User Score Information ===");
                for (Score score : userScore) {
                    logger.info("Test Type: {}, Score: {}", score.getTestType(), score.getScore());
                }
            } else {
                logger.info("No user score information provided");
            }

            // 생성된 프롬프트 로깅
            logger.info("=== Generated Prompt ===");
            logger.info(promptContent);
            logger.info("=== End of Prompt ===");

            Prompt prompt = new Prompt(Collections.singletonList(new UserMessage(promptContent)));

            ChatResponse response = openAiChatModel.call(prompt);
            Generation generation = response.getResult();
            String content = generation.getOutput().getText();

            return GptResponseParser.parse(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test using GPT", e);
        }
    }
}
