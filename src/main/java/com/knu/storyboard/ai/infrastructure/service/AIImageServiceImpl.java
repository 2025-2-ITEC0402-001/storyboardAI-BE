package com.knu.storyboard.ai.infrastructure.service;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.GenerateVideoRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.business.dto.VideoGenerationInitResponse;
import com.knu.storyboard.ai.business.dto.VideoGenerationStatusResponse;
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

    @Qualifier("aiWebClient")
    private final WebClient aiWebClient;

    @Override
    public byte[] generateStoryboardImage(GenerateImageRequest request) {
        log.info("Generating storyboard image with prompt: {}", request.prompt());

        try {
            return aiWebClient.post()
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

            return aiWebClient.post()
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

    @Override
    public VideoGenerationInitResponse generateVideo(GenerateVideoRequest request) {
        log.info("Generating video with frame number: {}", request.frameNumber());

        try {
            ByteArrayResource originImageResource = new ByteArrayResource(request.originImage().getBytes()) {
                @Override
                public String getFilename() {
                    return request.originImage().getOriginalFilename();
                }
            };

            ByteArrayResource samMaskResource = new ByteArrayResource(request.samMask().getBytes()) {
                @Override
                public String getFilename() {
                    return request.samMask().getOriginalFilename();
                }
            };

            ByteArrayResource trajectoryDataResource = new ByteArrayResource(request.trajectoryData().getBytes()) {
                @Override
                public String getFilename() {
                    return request.trajectoryData().getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("origin_image", originImageResource);
            formData.add("sam_mask", samMaskResource);
            formData.add("trajectory_data", trajectoryDataResource);
            formData.add("frame_number", request.frameNumber());

            log.info("Sending video generation request - origin_image: {}, sam_mask: {}, trajectory_data: {}, frame_number: {}", 
                    originImageResource.getFilename(), samMaskResource.getFilename(), 
                    trajectoryDataResource.getFilename(), request.frameNumber());

            return aiWebClient.post()
                    .uri("/generate")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .doOnNext(body -> log.error("Error response from AI server: {}", body))
                                    .then(response.createException()))
                    .bodyToMono(VideoGenerationInitResponse.class)
                    .block();
        } catch (IOException e) {
            log.error("Failed to read video files", e);
            throw new AIBadRequestException("Failed to read video files: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to generate video", e);
            throw new AIServiceException("Failed to generate video: " + e.getMessage(), e);
        }
    }

    @Override
    public VideoGenerationStatusResponse getVideoGenerationStatus(String jobId) {
        log.info("Checking video generation status for jobId: {}", jobId);

        try {
            return aiWebClient.get()
                    .uri("/status/{jobId}", jobId)
                    .retrieve()
                    .bodyToMono(VideoGenerationStatusResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to check video generation status", e);
            throw new AIServiceException("Failed to check status: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadGeneratedVideo(String jobId) {
        log.info("Downloading generated video for jobId: {}", jobId);

        try {
            return aiWebClient.get()
                    .uri("/api_jobs/{jobId}/output/generated_video.mp4", jobId)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to download generated video", e);
            throw new AIServiceException("Failed to download video: " + e.getMessage(), e);
        }
    }
}
