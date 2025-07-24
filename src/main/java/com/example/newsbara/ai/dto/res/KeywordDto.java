package com.example.newsbara.ai.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeywordDto {
    private String word;
    private String gptDefinition;
    private String gptDefinitionKo;
    private String bertDefinition;
    private String bertDefinitionKo;
    private String bertSource;
    private Double bertConfidence;
    private List<String> wordnetSenses;
    private List<String> wordnetSensesKo;
}
