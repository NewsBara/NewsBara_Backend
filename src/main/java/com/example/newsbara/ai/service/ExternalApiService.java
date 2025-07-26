package com.example.newsbara.ai.service;

import com.example.newsbara.ai.dto.res.AnalysisResDto;
import com.example.newsbara.ai.dto.res.PronounceResDto;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private String pronounceUrl;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * YouTube 영상 분석 API 호출
     */
    @Transactional
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


    @Transactional
    public PronounceResDto evaluatePronunciation(MultipartFile audioFile, String script) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("audio", audioFile.getBytes())
                    .filename(audioFile.getOriginalFilename())
                    .contentType(MediaType.MULTIPART_FORM_DATA);  // optional
            builder.part("script", script);

            log.info("Calling Pronunciation Evaluation API for script: {}",
                    script.substring(0, Math.min(50, script.length())));

            PronounceResDto result = webClient.post()
                    .uri(pronounceUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> {
                                log.error("API returned status: {}", clientResponse.statusCode());
                                return Mono.error(new GeneralException(ErrorStatus.EXTERNAL_API_ERROR));
                            })
                    .bodyToMono(PronounceResDto.class)
                    .block();
            if (result != null) {
                log.info("Successfully evaluated pronunciation. Score: {}", result.getScore());
            }
            return result;
        } catch (IOException e) {
            log.error("Error reading audio file", e);
            throw new GeneralException(ErrorStatus.FILE_READ_ERROR);
        } catch (WebClientResponseException e) {
            log.error("Pronunciation Evaluation API error, status: {}", e.getStatusCode(), e);
            throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
        } catch (Exception e) {
            log.error("Error calling Pronunciation Evaluation API", e);
            throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
        }
    }
}
