package com.knu.storyboard.ai.presentation.controller;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.presentation.api.AIImageApi;
import com.knu.storyboard.ai.presentation.dto.ReviseImageApiRequest;
import com.knu.storyboard.aiimage.business.dto.ImageGenerationTaskResponse;
import com.knu.storyboard.aiimage.business.service.AsyncAIImageService;
import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.auth.presentation.annotation.Login;
import com.knu.storyboard.auth.presentation.annotation.RequireAuth;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AIImageController implements AIImageApi {

    private final AsyncAIImageService asyncAIImageService;

    @Override
    @RequireAuth
    public ResponseEntity<ImageGenerationTaskResponse> generateImage(
            String prompt, 
            Integer height, 
            Integer width,
            Double guidanceScale, 
            Integer numInferenceSteps,
            Integer seed,
            @Parameter(hidden = true) @Login AuthUser authUser) {

        GenerateImageRequest request = new GenerateImageRequest(
                prompt, height, width, guidanceScale, numInferenceSteps, seed
        );

        ImageGenerationTaskResponse response = asyncAIImageService.generateStoryboardImageAsync(request);

        return ResponseEntity.accepted().body(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<ImageGenerationTaskResponse> reviseImage(
            ReviseImageApiRequest apiRequest,
            @Parameter(hidden = true) @Login AuthUser authUser) {

        ReviseImageRequest request = new ReviseImageRequest(
                apiRequest.image(),
                apiRequest.revisedPrompt(),
                apiRequest.strength(),
                apiRequest.guidanceScale(),
                apiRequest.numInferenceSteps(),
                apiRequest.seed()
        );

        ImageGenerationTaskResponse response = asyncAIImageService.reviseStoryboardImageAsync(request);

        return ResponseEntity.accepted().body(response);
    }
}
