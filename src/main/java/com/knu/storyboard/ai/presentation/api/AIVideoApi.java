package com.knu.storyboard.ai.presentation.api;

import com.knu.storyboard.ai.business.dto.VideoGenerationInitResponse;
import com.knu.storyboard.ai.business.dto.VideoGenerationStatusResponse;
import com.knu.storyboard.ai.presentation.dto.GenerateVideoApiRequest;
import com.knu.storyboard.auth.domain.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI 영상", description = "스토리보드 AI 영상 생성 API")
@RequestMapping("/api/ai/videos")
public interface AIVideoApi {

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "스토리보드 영상 생성", description = "이미지와 SAM 마스크를 사용하여 영상을 생성합니다.")
    ResponseEntity<VideoGenerationInitResponse> generateVideo(
            @ModelAttribute GenerateVideoApiRequest request,
            @Parameter(hidden = true) AuthUser authUser
    );

    @GetMapping("/status/{jobId}")
    @Operation(summary = "영상 생성 상태 조회", description = "영상 생성 작업의 현재 상태를 조회합니다.")
    ResponseEntity<VideoGenerationStatusResponse> getVideoStatus(
            @PathVariable String jobId,
            @Parameter(hidden = true) AuthUser authUser
    );

    @GetMapping("/download/{jobId}")
    @Operation(summary = "생성된 영상 다운로드", description = "완료된 영상을 다운로드합니다.")
    ResponseEntity<byte[]> downloadVideo(
            @PathVariable String jobId,
            @Parameter(hidden = true) AuthUser authUser
    );
}
