package com.knu.storyboard.aiimage.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImageGenerationEvent(
        String taskId,
        ImageGenerationStatus status,
        String message,
        Integer progress,
        String imageUrl,
        String errorMessage,
        LocalDateTime timestamp
) {
    public static ImageGenerationEvent pending(String taskId) {
        return new ImageGenerationEvent(
                taskId,
                ImageGenerationStatus.PENDING,
                "이미지 생성 요청이 접수되었습니다",
                0,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ImageGenerationEvent inProgress(String taskId, String message, Integer progress) {
        return new ImageGenerationEvent(
                taskId,
                ImageGenerationStatus.IN_PROGRESS,
                message,
                progress,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ImageGenerationEvent uploading(String taskId) {
        return new ImageGenerationEvent(
                taskId,
                ImageGenerationStatus.UPLOADING,
                "이미지를 S3에 업로드중입니다",
                90,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ImageGenerationEvent completed(String taskId, String imageUrl) {
        return new ImageGenerationEvent(
                taskId,
                ImageGenerationStatus.COMPLETED,
                "이미지 생성이 완료되었습니다",
                100,
                imageUrl,
                null,
                LocalDateTime.now()
        );
    }

    public static ImageGenerationEvent failed(String taskId, String errorMessage) {
        return new ImageGenerationEvent(
                taskId,
                ImageGenerationStatus.FAILED,
                "이미지 생성에 실패했습니다",
                null,
                null,
                errorMessage,
                LocalDateTime.now()
        );
    }
}
