package com.knu.storyboard.ai.presentation.controller;

import com.knu.storyboard.ai.business.dto.GenerateVideoRequest;
import com.knu.storyboard.ai.business.dto.VideoGenerationInitResponse;
import com.knu.storyboard.ai.business.dto.VideoGenerationStatusResponse;
import com.knu.storyboard.ai.business.port.AIImageService;
import com.knu.storyboard.ai.presentation.api.AIVideoApi;
import com.knu.storyboard.ai.presentation.dto.GenerateVideoApiRequest;
import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.auth.presentation.annotation.Login;
import com.knu.storyboard.auth.presentation.annotation.RequireAuth;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AIVideoController implements AIVideoApi {

    private final AIImageService aiImageService;

    @Override
    @RequireAuth
    public ResponseEntity<VideoGenerationInitResponse> generateVideo(
            GenerateVideoApiRequest apiRequest,
            @Parameter(hidden = true) @Login AuthUser authUser) {

        GenerateVideoRequest request = new GenerateVideoRequest(
                apiRequest.originImage(),
                apiRequest.samMask(),
                apiRequest.trajectoryData(),
                apiRequest.frameNumber()
        );

        VideoGenerationInitResponse response = aiImageService.generateVideo(request);

        return ResponseEntity.accepted().body(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<VideoGenerationStatusResponse> getVideoStatus(
            String jobId,
            @Parameter(hidden = true) @Login AuthUser authUser) {

        VideoGenerationStatusResponse response = aiImageService.getVideoGenerationStatus(jobId);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<byte[]> downloadVideo(
            String jobId,
            @Parameter(hidden = true) @Login AuthUser authUser) {

        byte[] videoData = aiImageService.downloadGeneratedVideo(jobId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated_video.mp4")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(videoData);
    }
}
