package com.example.artwork.client;

import com.example.artwork.config.ScoringProperties;
import com.example.artwork.dto.ScoreRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ScoringClient {

    private final RestTemplate restTemplate;
    private final ScoringProperties properties;

    public ScoringClient(RestTemplate restTemplate, ScoringProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void submitScore(ScoreRequest request) {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return;
        }

        String url = baseUrl + "/scoring/scores";
        restTemplate.postForObject(url, request, String.class);
    }
}
