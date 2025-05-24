package com.example.newsbara.test.service;

import com.example.newsbara.score.domain.Score;
import com.example.newsbara.test.util.GptPromptBuilder;
import com.example.newsbara.test.util.GptResponseParser;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class GptService {

    private final OpenAiChatModel openAiChatModel;

    @Autowired
    public GptService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    public Map<String, String> generateTest(String transcript, Score userScore) {
        try {
            String promptContent = GptPromptBuilder.buildPrompt(transcript, userScore);
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
