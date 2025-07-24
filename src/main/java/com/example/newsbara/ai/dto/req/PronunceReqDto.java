package com.example.newsbara.ai.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PronunceReqDto {
    private MultipartFile audio;
    private String script;
}
