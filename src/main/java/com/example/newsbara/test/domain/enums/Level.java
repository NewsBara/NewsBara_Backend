package com.example.newsbara.test.domain.enums;

// level을 사용할지, score을 사용할지 논의 필요.
// 만약 level을 사용할 시 도메인으로 뺄지 생각해봐야함
// 만약 score를 사용할 시 level을 제거하기

public enum Level {
    LEVEL_1("1"),
    LEVEL_2("2"),
    LEVEL_3("3"),
    LEVEL_4("4"),
    LEVEL_5("5"),
    LEVEL_6("6"),
    LEVEL_7("7");

    private final String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}