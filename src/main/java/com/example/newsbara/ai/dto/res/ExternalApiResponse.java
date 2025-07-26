package com.example.newsbara.ai.dto.res;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponse {
    private String code;
    private boolean isSuccess;
    private String message;
    private RecommendResDto result;
}