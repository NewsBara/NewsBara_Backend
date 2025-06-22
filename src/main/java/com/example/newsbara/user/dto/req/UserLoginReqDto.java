package com.example.newsbara.user.dto.req;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginReqDto {
    private String email;
    private String password;
}
