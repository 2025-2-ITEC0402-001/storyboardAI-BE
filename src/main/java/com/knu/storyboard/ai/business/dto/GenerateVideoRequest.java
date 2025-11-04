package com.knu.storyboard.ai.business.dto;

import org.springframework.web.multipart.MultipartFile;

public record GenerateVideoRequest(
        MultipartFile originImage,
        MultipartFile samMask,
        MultipartFile trajectoryData,
        Integer frameNumber
) {
    public static GenerateVideoRequest createDefault(
            MultipartFile originImage,
            MultipartFile samMask,
            MultipartFile trajectoryData
    ) {
        return new GenerateVideoRequest(
                originImage,
                samMask,
                trajectoryData,
                14
        );
    }
}
