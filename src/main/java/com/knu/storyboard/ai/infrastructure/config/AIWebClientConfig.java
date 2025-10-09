package com.knu.storyboard.ai.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AIWebClientConfig {

    @Value("${storyboard.ai.generate.url}")
    private String generateServiceUrl;

    @Value("${storyboard.ai.revise.url}")
    private String reviseServiceUrl;

    @Bean
    public WebClient generateWebClient() {
        return WebClient.builder()
                .baseUrl(generateServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }

    @Bean
    public WebClient reviseWebClient() {
        return WebClient.builder()
                .baseUrl(reviseServiceUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }
}
