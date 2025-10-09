package com.knu.storyboard.aiimage.business.dto;

import lombok.Getter;

@Getter
public enum ImageGenerationStatus {
    PENDING("대기중"),
    IN_PROGRESS("생성중"),
    UPLOADING("업로드중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    ImageGenerationStatus(String description) {
        this.description = description;
    }
}
