package com.knu.storyboard.ai.business.dto;

import org.springframework.web.multipart.MultipartFile;

public record ReviseImageRequest(
        MultipartFile image,
        String revisedPrompt,
        Double strength,
        Double guidanceScale,
        Integer numInferenceSteps,
        Integer seed
) {
    public static ReviseImageRequest createDefault(MultipartFile image, String revisedPrompt) {
        return new ReviseImageRequest(
                image,
                revisedPrompt,
                0.9,
                3.5,
                25,
                0
        );
    }
}