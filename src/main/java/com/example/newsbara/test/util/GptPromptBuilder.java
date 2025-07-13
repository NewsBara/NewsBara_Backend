package com.example.newsbara.test.util;

import com.example.newsbara.score.domain.Score;

import java.util.List;

public class GptPromptBuilder {

    public static String buildPrompt(String transcript, List<Score> userScore) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Based on the following YouTube video transcript, please create ")
                .append("a fill-in-the-blank summary question");

        // userScore가 null이 아니고 비어있지 않은 경우 처리
        if (userScore != null && !userScore.isEmpty()) {
            // 각 점수별로 난이도 조정 (여러 점수가 있는 경우)
            StringBuilder levelInfo = new StringBuilder();
            for (Score score : userScore) {
                if (levelInfo.length() > 0) {
                    levelInfo.append(", ");
                }
                levelInfo.append(score.getTestType()).append(" ").append(score.getScore());
            }
            prompt.append(" considering user's proficiency levels: ").append(levelInfo);
        }

        prompt.append(". ")
                .append("Create a 3-sentence summary of the key points, but remove one important word and replace it with a blank. ")
                .append("Format your response in JSON with the following structure: ")
                .append("{\n")
                .append("  \"summary\": \"A 3-sentence summary with a _____ (blank)\",\n")
                .append("  \"answer\": \"The correct word or phrase that goes in the blank\",\n")
                .append("  \"explanation\": \"Brief explanation of why this is the correct answer in Korean\"\n")
                .append("}")
                .append("\n\nTranscript: ").append(transcript);

        return prompt.toString();
    }
}
