package com.example.newsbara.ai.dto.req;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendReqDto {
    private List<VideoHistoryDto> historyList;
}
