package com.knu.storyboard.ai.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoGenerationInitResponse(
        String status,
        @JsonProperty("job_id")
        String jobId,
        @JsonProperty("status_url")
        String statusUrl
) {
}
