package com.knu.storyboard.ai.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoGenerationStatusResponse(
        String status,
        @JsonProperty("video_url")
        String videoUrl
) {
}
