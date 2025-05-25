package com.example.newsbara.history.util;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeDurationParser {

    public static String parse(String isoDuration) {
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(isoDuration);
        if (!matcher.matches()) return "";

        StringBuilder result = new StringBuilder();
        String hour = matcher.group(1);
        String minute = matcher.group(2);
        String second = matcher.group(3);

        if (hour != null) result.append(hour).append("시간 ");
        if (minute != null) result.append(minute).append("분 ");
        if (second != null) result.append(second).append("초");

        return result.toString().trim();
    }

    public static int parseToSeconds(String isoDuration) {
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(isoDuration);
        if (!matcher.matches()) return 0;

        int hours = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
        int minutes = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
        int seconds = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

        return hours * 3600 + minutes * 60 + seconds;
    }
}
