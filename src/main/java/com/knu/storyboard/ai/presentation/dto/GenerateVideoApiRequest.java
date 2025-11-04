package com.knu.storyboard.ai.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "영상 생성 요청")
public record GenerateVideoApiRequest(
        @Schema(description = "원본 이미지", required = true)
        MultipartFile originImage,

        @Schema(description = "SAM 마스크 이미지", required = true)
        MultipartFile samMask,

        @Schema(description = "트래젝토리 데이터 (JSON 파일)", required = true)
        MultipartFile trajectoryData,

        @Schema(description = "프레임 수", example = "14", defaultValue = "14")
        Integer frameNumber
) {
}
