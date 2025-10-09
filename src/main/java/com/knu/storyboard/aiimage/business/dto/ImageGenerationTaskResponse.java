package com.knu.storyboard.aiimage.business.dto;

public record ImageGenerationTaskResponse(
        String taskId,
        String message
) {
    public static ImageGenerationTaskResponse of(String taskId) {
        return new ImageGenerationTaskResponse(
                taskId,
                "이미지 생성이 시작되었습니다. SSE 스트림을 통해 진행 상황을 확인하세요."
        );
    }
}
