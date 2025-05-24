package com.example.newsbara.test.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TestResDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestResponse {
        private String videoId;
        private String summary;
        private String answer;
        private String explanation;
    }
}