package com.example.newsbara.ai.service;

import com.example.newsbara.ai.dto.req.AnalysisReqDto;
import com.example.newsbara.ai.dto.res.AnalysisResDto;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalApiService {
    private final WebClient webClient;

    @Value("${external.api.script.url}")
    private String scriptUrl;

    @Value("${external.api.pronounce.url}")
    private String pronunceUrl;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * YouTube 영상 분석 API 호출
     */
    public List<AnalysisResDto> analyzeVideo(String videoId) {
        try {
            Map<String, String> requestBody = Map.of("video_id", videoId);

            log.info("Calling YouTube Analysis API for videoId: {}", videoId);

            List<AnalysisResDto> result = webClient.post()
                    .uri(scriptUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> {
                                log.error("YouTube Analysis API returned status: {}", clientResponse.statusCode());
                                return Mono.error(new GeneralException(ErrorStatus.EXTERNAL_API_ERROR));
                            })
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<AnalysisResDto>>() {})
                    .block();

            log.info("Successfully analyzed video. Found {} sentences", result.size());
            return result;

        } catch (WebClientResponseException e) {
            log.error("YouTube Analysis API error for videoId: {}, status: {}", videoId, e.getStatusCode(), e);
            throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
        } catch (Exception e) {
            log.error("Error calling YouTube Analysis API for videoId: {}", videoId, e);
            throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
        }
    }


}
