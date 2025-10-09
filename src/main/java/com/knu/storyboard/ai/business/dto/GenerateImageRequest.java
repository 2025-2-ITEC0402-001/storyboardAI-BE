package com.knu.storyboard.ai.business.dto;

public record GenerateImageRequest(
        String prompt,
        Integer height,
        Integer width,
        Double guidanceScale,
        Integer numInferenceSteps,
        Integer seed
) {
    public static GenerateImageRequest createDefault(String prompt) {
        return new GenerateImageRequest(
                prompt,
                1536,
                1024,
                3.5,
                20,
                0
        );
    }
}