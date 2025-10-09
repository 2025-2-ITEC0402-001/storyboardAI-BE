package com.knu.storyboard.ai.business.port;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;

public interface AIImageService {

    byte[] generateStoryboardImage(GenerateImageRequest request);

    byte[] reviseStoryboardImage(ReviseImageRequest request);
}
