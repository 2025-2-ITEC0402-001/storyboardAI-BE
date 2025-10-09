package com.knu.storyboard.ai.infrastructure.service;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.business.port.AIImageService;
import com.knu.storyboard.ai.exception.AIBadRequestException;
import com.knu.storyboard.ai.exception.AIServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIImageServiceImpl implements AIImageService {

    @Qualifier("generateWebClient")
    private final WebClient generateWebClient;

    @Qualifier("reviseWebClient")
    private final WebClient reviseWebClient;

    @Override
    public byte[] generateStoryboardImage(GenerateImageRequest request) {
        log.info("Generating storyboard image with prompt: {}", request.prompt());

        try {
            return generateWebClient.post()
                    .uri("/generate-storyboard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to generate storyboard image", e);
            throw new AIServiceException("Failed to generate image: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] reviseStoryboardImage(ReviseImageRequest request) {
        log.info("Revising storyboard image with prompt: {}", request.revisedPrompt());

        try {
            ByteArrayResource imageResource = new ByteArrayResource(request.image().getBytes()) {
                @Override
                public String getFilename() {
                    return request.image().getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("image", imageResource);
            formData.add("revised_prompt", request.revisedPrompt());
            formData.add("strength", request.strength());
            formData.add("guidance_scale", request.guidanceScale());
            formData.add("num_inference_steps", request.numInferenceSteps());
            formData.add("seed", request.seed());

            return reviseWebClient.post()
                    .uri("/revise-storyboard")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (IOException e) {
            log.error("Failed to read image file", e);
            throw new AIBadRequestException("Failed to read image file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to revise storyboard image", e);
            throw new AIServiceException("Failed to revise image: " + e.getMessage(), e);
        }
    }
}
