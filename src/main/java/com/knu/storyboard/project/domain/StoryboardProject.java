package com.knu.storyboard.project.domain;

import com.knu.storyboard.project.business.dto.ProjectEntityDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoryboardProject {
    private final UUID id;
    private final String title;
    private final String description;
    private final UUID ownerId;
    private final String canvas;
    private final String thumbnail;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static StoryboardProject create(String title, String description, UUID ownerId) {
        return StoryboardProject.builder()
                .title(title)
                .description(description)
                .ownerId(ownerId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static StoryboardProject fromDto(ProjectEntityDto dto) {
        return StoryboardProject.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .ownerId(dto.getOwnerId())
                .canvas(dto.getCanvas())
                .thumbnail(dto.getThumbnail())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public StoryboardProject updateTitle(String newTitle) {
        return StoryboardProject.builder()
                .id(this.id)
                .title(newTitle)
                .description(this.description)
                .ownerId(this.ownerId)
                .canvas(this.canvas)
                .thumbnail(this.thumbnail)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public StoryboardProject updateDescription(String newDescription) {
        return StoryboardProject.builder()
                .id(this.id)
                .title(this.title)
                .description(newDescription)
                .ownerId(this.ownerId)
                .canvas(this.canvas)
                .thumbnail(this.thumbnail)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public StoryboardProject updateCanvas(String newCanvas) {
        return StoryboardProject.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .ownerId(this.ownerId)
                .canvas(newCanvas)
                .thumbnail(this.thumbnail)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public StoryboardProject updateThumbnail(String newThumbnail) {
        return StoryboardProject.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .ownerId(this.ownerId)
                .canvas(this.canvas)
                .thumbnail(newThumbnail)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isOwnedBy(UUID userId) {
        return this.ownerId.equals(userId);
    }

    public ProjectEntityDto toEntityDto() {
        return ProjectEntityDto.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .ownerId(this.ownerId)
                .canvas(this.canvas)
                .thumbnail(this.thumbnail)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
