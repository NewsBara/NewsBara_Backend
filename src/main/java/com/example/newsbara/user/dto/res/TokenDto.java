package com.example.newsbara.user.dto.res;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {
    private String accessToken;

    @Builder
    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
