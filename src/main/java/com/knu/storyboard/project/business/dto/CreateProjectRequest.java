package com.knu.storyboard.project.business.dto;

public record CreateProjectRequest(
        String title,
        String description
) {
}