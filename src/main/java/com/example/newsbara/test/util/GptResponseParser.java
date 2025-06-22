package com.example.newsbara.test.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GptResponseParser {

    public static Map<String, String> parse(String content) {
        Map<String, String> results = new HashMap<>();

        String jsonPattern = "\\{[\\s\\S]*?\\}";
        Pattern pattern = Pattern.compile(jsonPattern);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String jsonContent = matcher.group(0);
            results.put("summary", extractJsonField(jsonContent, "summary"));
            results.put("answer", extractJsonField(jsonContent, "answer"));
            results.put("explanation", extractJsonField(jsonContent, "explanation"));
        } else {
            results.put("summary", "Could not parse summary from response");
            results.put("answer", "Could not parse answer from response");
            results.put("explanation", "Could not parse explanation from response");
        }

        return results;
    }

    private static String extractJsonField(String json, String fieldName) {
        String regex = "\"" + fieldName + "\"\\s*:\\s*\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1).replace("\\\"", "\"").replace("\\n", "\n");
        }
        return "";
    }
}
