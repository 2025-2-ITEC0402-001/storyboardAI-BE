package com.knu.storyboard.ai.presentation.api;

import com.knu.storyboard.ai.presentation.dto.ReviseImageApiRequest;
import com.knu.storyboard.aiimage.business.dto.ImageGenerationTaskResponse;
import com.knu.storyboard.auth.domain.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "AI 이미지", description = "스토리보드 AI 이미지 생성/수정 API")
@RequestMapping("/api/ai/images")
public interface AIImageApi {

    @PostMapping("/generate")
    @Operation(summary = "스토리보드 이미지 생성", description = "프롬프트를 사용하여 스토리보드 이미지를 비동기로 생성합니다. SSE 스트림으로 진행 상황을 확인하세요.")
    ResponseEntity<ImageGenerationTaskResponse> generateImage(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "1536") Integer height,
            @RequestParam(defaultValue = "1024") Integer width,
            @RequestParam(defaultValue = "3.5") Double guidanceScale,
            @RequestParam(defaultValue = "20") Integer numInferenceSteps,
            @RequestParam(defaultValue = "0") Integer seed,
            @Parameter(hidden = true) AuthUser authUser
    );

    @PostMapping(value = "/revise", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "스토리보드 이미지 수정", description = "기존 이미지를 수정된 프롬프트로 비동기로 다시 생성합니다. SSE 스트림으로 진행 상황을 확인하세요.")
    ResponseEntity<ImageGenerationTaskResponse> reviseImage(
            @ModelAttribute ReviseImageApiRequest request,
            @Parameter(hidden = true) AuthUser authUser
    );
}
