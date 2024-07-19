package com.diary_server.service;

import com.diary_server.dto.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OpenAiClient {
    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String apiModel;

    public OpenAiClient(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.build();
    }

    public String generateComment(List<Message> messages) {
        OpenAiRequest requestBody = new OpenAiRequest(apiModel, messages);
        return Objects.requireNonNull(webClient.post()
                        .uri(apiUrl)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(OpenAiResponse.class)
                        .block())
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OpenAiResponse {
        private List<Choice> choices;

        @Getter @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        private static class Choice {
            private Message message;
        }
    }
}
