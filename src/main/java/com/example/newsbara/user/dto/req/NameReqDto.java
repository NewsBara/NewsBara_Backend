package com.example.newsbara.user.dto.req;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class NameReqDto {
    @NotNull
    private String name;
}
