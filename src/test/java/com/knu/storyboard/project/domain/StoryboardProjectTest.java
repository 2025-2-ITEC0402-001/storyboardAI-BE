package com.knu.storyboard.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoryboardProjectTest {

    @Test
    @DisplayName("스토리보드 프로젝트를 생성할 수 있다")
    void create_ShouldCreateStoryboardProject() {
        // Given
        String title = "테스트 프로젝트";
        String description = "테스트 설명";
        UUID ownerId = UUID.randomUUID();

        // When
        StoryboardProject project = StoryboardProject.create(title, description, ownerId);

        // Then
        assertThat(project.getTitle()).isEqualTo(title);
        assertThat(project.getDescription()).isEqualTo(description);
        assertThat(project.getOwnerId()).isEqualTo(ownerId);
        assertThat(project.getCanvas()).isEqualTo("{\"nodes\":[], \"connections\":[]}");
        assertThat(project.isOwnedBy(ownerId)).isTrue();
    }

    @Test
    @DisplayName("프로젝트 제목을 업데이트할 수 있다")
    void updateTitle_ShouldUpdateTitle() {
        // Given
        StoryboardProject project = StoryboardProject.create("원본 제목", "설명", UUID.randomUUID());
        String newTitle = "새로운 제목";

        // When
        StoryboardProject updatedProject = project.updateTitle(newTitle);

        // Then
        assertThat(updatedProject.getTitle()).isEqualTo(newTitle);
        assertThat(updatedProject.getDescription()).isEqualTo(project.getDescription());
        assertThat(updatedProject.getCanvas()).isEqualTo(project.getCanvas());
    }

    @Test
    @DisplayName("프로젝트 캔버스를 업데이트할 수 있다")
    void updateCanvas_ShouldUpdateCanvas() {
        // Given
        StoryboardProject project = StoryboardProject.create("제목", "설명", UUID.randomUUID());
        String newCanvas = "{\"nodes\":[{\"id\":\"1\",\"prompt\":\"test\"}], \"connections\":[]}";

        // When
        StoryboardProject updatedProject = project.updateCanvas(newCanvas);

        // Then
        assertThat(updatedProject.getCanvas()).isEqualTo(newCanvas);
        assertThat(updatedProject.getTitle()).isEqualTo(project.getTitle());
    }
}