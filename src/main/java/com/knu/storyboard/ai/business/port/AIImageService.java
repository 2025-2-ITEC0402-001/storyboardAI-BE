package com.knu.storyboard.ai.business.port;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.GenerateVideoRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.business.dto.VideoGenerationInitResponse;
import com.knu.storyboard.ai.business.dto.VideoGenerationStatusResponse;

public interface AIImageService {

    byte[] generateStoryboardImage(GenerateImageRequest request);

    byte[] reviseStoryboardImage(ReviseImageRequest request);

    VideoGenerationInitResponse generateVideo(GenerateVideoRequest request);

    VideoGenerationStatusResponse getVideoGenerationStatus(String jobId);

    byte[] downloadGeneratedVideo(String jobId);
}
