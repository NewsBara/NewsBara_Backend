package com.example.newsbara.test.util;

import com.example.newsbara.score.domain.Score;

public class GptPromptBuilder {

    public static String buildPrompt(String transcript, Score userScore) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Based on the following YouTube video transcript, please create ")
                .append("a fill-in-the-blank summary question");

        if (userScore != null) {
            int score = userScore.getScore();
            String testType = String.valueOf(userScore.getTestType());
            prompt.append(" at ").append(testType).append(" ").append(score).append(" level");
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
