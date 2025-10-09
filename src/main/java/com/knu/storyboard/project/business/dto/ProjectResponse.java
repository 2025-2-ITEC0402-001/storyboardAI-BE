package com.knu.storyboard.project.business.dto;

import com.knu.storyboard.project.domain.StoryboardProject;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String title,
        String description,
        UUID ownerId,
        String canvas,
        String thumbnail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(StoryboardProject project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getOwnerId(),
                project.getCanvas(),
                project.getThumbnail(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}