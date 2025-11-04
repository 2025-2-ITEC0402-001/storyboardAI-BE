package com.knu.storyboard.ai.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "이미지 수정 요청")
public record ReviseImageApiRequest(
        @Schema(description = "수정할 원본 이미지", required = true)
        MultipartFile image,

        @Schema(description = "수정 프롬프트", example = "cat is wearing a wizard hat", required = true)
        String revisedPrompt,

        @Schema(description = "수정 강도 (0.0 ~ 1.0)", example = "0.9", defaultValue = "0.9")
        Double strength,

        @Schema(description = "가이던스 스케일", example = "3.5", defaultValue = "3.5")
        Double guidanceScale,

        @Schema(description = "추론 스텝 수", example = "25", defaultValue = "25")
        Integer numInferenceSteps,

        @Schema(description = "시드 값", example = "0", defaultValue = "0")
        Integer seed
) {
}
