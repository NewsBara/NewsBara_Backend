package com.example.newsbara.ai.dto.res;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = DifferenceDto.DifferenceDtoDeserializer.class)
public class DifferenceDto {
    private String expected;
    private String pronounced;

    public static class DifferenceDtoDeserializer extends JsonDeserializer<DifferenceDto> {
        @Override
        public DifferenceDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken() == JsonToken.START_ARRAY) {
                // 배열 형태의 데이터 처리: ["expected", "pronounced"]
                String[] values = p.readValueAs(String[].class);
                if (values.length >= 2) {
                    return new DifferenceDto(values[0], values[1]);
                } else if (values.length == 1) {
                    return new DifferenceDto(values[0], "");
                } else {
                    return new DifferenceDto("", "");
                }
            } else if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                // 객체 형태의 데이터 처리: {"expected": "...", "pronounced": "..."}
                DifferenceDto dto = new DifferenceDto();
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = p.getCurrentName();
                    p.nextToken();
                    if ("expected".equals(fieldName)) {
                        dto.setExpected(p.getText());
                    } else if ("pronounced".equals(fieldName)) {
                        dto.setPronounced(p.getText());
                    }
                }
                return dto;
            }
            throw new IOException("Cannot deserialize DifferenceDto from " + p.getCurrentToken());
        }
    }
}