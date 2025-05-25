package com.example.newsbara.history.dto.req;

import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryReqDto {
    private String videoId;
    private Status status;
}
