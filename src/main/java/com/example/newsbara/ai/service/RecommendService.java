package com.example.newsbara.ai.service;

import com.example.newsbara.ai.dto.req.RecommendReqDto;
import com.example.newsbara.ai.dto.req.VideoHistoryDto;
import com.example.newsbara.ai.dto.res.RecommendResDto;
import com.example.newsbara.ai.dto.res.ExternalApiResponse;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.history.domain.WatchHistory;
import com.example.newsbara.history.repository.HistoryRepository;
import com.example.newsbara.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final HistoryRepository watchHistoryRepository;

    @Value("${external.api.recommend.url}")
    private String recommendUrl;

    public RecommendService(WebClient webClient, UserRepository userRepository, HistoryRepository watchHistoryRepository) {
        this.webClient = webClient;
        this.userRepository = userRepository;
        this.watchHistoryRepository = watchHistoryRepository;
    }

    @Transactional
    public RecommendResDto getRecommendations(Principal principal) {
        try {
            // 1. 사용자 정보 조회
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            // 2. 사용자 시청기록 조회
            List<WatchHistory> watchHistories = watchHistoryRepository.findByUserOrderByCreatedAtDesc(user);

            if (watchHistories.isEmpty()) {
                log.info("No watch history found for user: {}", user.getEmail());
                return RecommendResDto.builder()
                        .recommendList(List.of())
                        .build();
            }

            // 3. 시청기록을 ML API 요청 형태로 변환
            List<VideoHistoryDto> historyList = watchHistories.stream()
                    .map(history -> new VideoHistoryDto(
                            history.getVideoId(),
                            history.getTitle(),
                            history.getChannel(),
                            history.getCategory()
                    ))
                    .collect(Collectors.toList());

            RecommendReqDto requestDto = new RecommendReqDto(historyList);

            log.info("Calling ML Recommendation API with {} history items for user: {}",
                    historyList.size(), user.getEmail());

            // JsonNode로 응답 받기
            String responseBody = webClient.post()
                    .uri(recommendUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestDto))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            clientResponse -> {
                                log.error("ML Recommendation API returned client error: {}", clientResponse.statusCode());
                                return Mono.error(new GeneralException(ErrorStatus.EXTERNAL_API_ERROR));
                            })
                    .onStatus(status -> status.is5xxServerError(),
                            clientResponse -> {
                                log.error("ML Recommendation API returned server error: {}", clientResponse.statusCode());
                                return Mono.error(new GeneralException(ErrorStatus.EXTERNAL_API_ERROR));
                            })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            log.info("Raw API Response: {}", responseBody);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // isSuccess 확인
            boolean isSuccess = rootNode.path("isSuccess").asBoolean(false);
            if (!isSuccess) {
                log.error("ML Recommendation API returned unsuccessful response: {}",
                        rootNode.path("message").asText());
                throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
            }

            // result 노드에서 RecommendResDto 추출
            JsonNode resultNode = rootNode.path("result");
            RecommendResDto result = objectMapper.treeToValue(resultNode, RecommendResDto.class);

            log.info("Successfully got {} recommendations for user: {}",
                    result != null && result.getRecommendList() != null ? result.getRecommendList().size() : 0,
                    user.getEmail());

            return result != null ? result : RecommendResDto.builder().recommendList(List.of()).build();

        } catch (WebClientResponseException e) {
            log.error("ML Recommendation API error, status: {}, body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
        } catch (Exception e) {
            log.error("Error processing recommendation request for user: {}",
                    principal.getName(), e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}