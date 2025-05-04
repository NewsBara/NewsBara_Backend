package com.example.newsbara.user.dto.req;

import com.example.newsbara.user.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignupReqDto {
    private String email;
    private String password;
    private String phone;
    private String name;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .phone(this.phone)
                .name(this.name)
                .point(0)
                .build();
    }
}

