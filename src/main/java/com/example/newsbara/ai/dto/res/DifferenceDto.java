package com.example.newsbara.ai.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifferenceDto {
    private String expected;
    private String pronounced;
}
