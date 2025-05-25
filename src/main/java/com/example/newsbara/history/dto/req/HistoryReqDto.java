package com.example.newsbara.history.dto.req;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.domain.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HistoryReqDto {
    private String videoId;
    private Status status;
}
