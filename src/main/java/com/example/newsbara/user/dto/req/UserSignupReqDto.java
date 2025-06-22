package com.example.newsbara.user.dto.req;

import com.example.newsbara.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor  // 이 어노테이션 추가!
@Builder
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