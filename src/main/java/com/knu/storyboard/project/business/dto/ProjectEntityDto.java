package com.knu.storyboard.project.business.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectEntityDto {
    private final UUID id;
    private final String title;
    private final String description;
    private final UUID ownerId;
    private final String canvas;
    private final String thumbnail;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProjectEntityDto create(String title, String description, UUID ownerId) {
        return ProjectEntityDto.builder()
                .title(title)
                .description(description)
                .ownerId(ownerId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ProjectEntityDto create(UUID id, String title, String description,
                                          UUID ownerId, String canvas, String thumbnail,
                                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        return ProjectEntityDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .ownerId(ownerId)
                .canvas(canvas)
                .thumbnail(thumbnail)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
