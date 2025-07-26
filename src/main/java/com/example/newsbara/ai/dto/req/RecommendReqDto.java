package com.example.newsbara.ai.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendReqDto {
    @NotNull(message = "History list cannot be null")
    @NotEmpty(message = "History list cannot be empty")
    private List<VideoHistoryDto> historyList;
}
