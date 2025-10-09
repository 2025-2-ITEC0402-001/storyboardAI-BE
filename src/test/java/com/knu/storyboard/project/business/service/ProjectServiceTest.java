package com.knu.storyboard.project.business.service;

import com.knu.storyboard.common.file.FileStorageService;
import com.knu.storyboard.config.TestEnvironmentConfig;
import com.knu.storyboard.project.business.dto.CreateProjectRequest;
import com.knu.storyboard.project.business.dto.ProjectEntityDto;
import com.knu.storyboard.project.business.dto.ProjectResponse;
import com.knu.storyboard.project.business.dto.UpdateProjectRequest;
import com.knu.storyboard.project.business.port.ProjectRepository;
import com.knu.storyboard.project.exception.ProjectBadRequestException;
import com.knu.storyboard.project.exception.ProjectForbiddenException;
import com.knu.storyboard.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestEnvironmentConfig.class})
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FileStorageService fileStorageService;

    private ProjectService projectService;
    private UUID testUserId;
    private UUID testProjectId;
    private UUID otherUserId;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, fileStorageService);
        testUserId = UUID.randomUUID();
        testProjectId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("프로젝트 생성 성공 테스트")
    void createProject_ShouldReturnProjectResponse_WhenRequestIsValid() {
        // Given
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Test Description");
        ProjectEntityDto savedDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.save(any(ProjectEntityDto.class))).thenReturn(savedDto);

        // When
        ProjectResponse response = projectService.createProject(request, testUserId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Test Project");
        assertThat(response.description()).isEqualTo("Test Description");
        assertThat(response.ownerId()).isEqualTo(testUserId);
        verify(projectRepository).save(any(ProjectEntityDto.class));
    }

    @Test
    @DisplayName("프로젝트 조회 성공 테스트")
    void getProjectById_ShouldReturnProject_WhenProjectExistsAndUserIsOwner() {
        // Given
        ProjectEntityDto projectDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(projectDto));

        // When
        ProjectResponse response = projectService.getProjectById(testProjectId, testUserId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testProjectId);
        assertThat(response.ownerId()).isEqualTo(testUserId);
        verify(projectRepository).findById(testProjectId);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 조회 시 ProjectNotFoundException 발생")
    void getProjectById_ShouldThrowProjectNotFoundException_WhenProjectNotExists() {
        // Given
        when(projectRepository.findById(testProjectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectById(testProjectId, testUserId))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining("Project not found with id");

        verify(projectRepository).findById(testProjectId);
    }

    @Test
    @DisplayName("권한 없는 사용자가 프로젝트 조회 시 ProjectForbiddenException 발생")
    void getProjectById_ShouldThrowProjectForbiddenException_WhenUserIsNotOwner() {
        // Given
        ProjectEntityDto projectDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(projectDto));

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectById(testProjectId, otherUserId))
                .isInstanceOf(ProjectForbiddenException.class)
                .hasMessageContaining("Access denied");

        verify(projectRepository).findById(testProjectId);
    }

    @Test
    @DisplayName("소유자별 프로젝트 목록 조회 테스트")
    void getProjectsByOwnerId_ShouldReturnProjectList() {
        // Given
        List<ProjectEntityDto> projectDtos = List.of(
                ProjectEntityDto.builder()
                        .id(UUID.randomUUID())
                        .title("Project 1")
                        .description("Description 1")
                        .ownerId(testUserId)
                        .canvas("{\"nodes\":[], \"connections\":[]}")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                ProjectEntityDto.builder()
                        .id(UUID.randomUUID())
                        .title("Project 2")
                        .description("Description 2")
                        .ownerId(testUserId)
                        .canvas("{\"nodes\":[], \"connections\":[]}")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        when(projectRepository.findByOwnerId(testUserId)).thenReturn(projectDtos);

        // When
        List<ProjectResponse> responses = projectService.getProjectsByOwnerId(testUserId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).title()).isEqualTo("Project 1");
        assertThat(responses.get(1).title()).isEqualTo("Project 2");
        verify(projectRepository).findByOwnerId(testUserId);
    }

    @Test
    @DisplayName("프로젝트 업데이트 성공 테스트")
    void updateProject_ShouldReturnUpdatedProject_WhenRequestIsValid() {
        // Given
        UpdateProjectRequest request = new UpdateProjectRequest("Updated Title", "Updated Description");
        ProjectEntityDto existingDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Original Title")
                .description("Original Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProjectEntityDto updatedDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Updated Title")
                .description("Updated Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .createdAt(existingDto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(existingDto));
        when(projectRepository.save(any(ProjectEntityDto.class))).thenReturn(updatedDto);

        // When
        ProjectResponse response = projectService.updateProject(testProjectId, request, testUserId);

        // Then
        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.description()).isEqualTo("Updated Description");
        verify(projectRepository).findById(testProjectId);
        verify(projectRepository).save(any(ProjectEntityDto.class));
    }

    @Test
    @DisplayName("프로젝트 삭제 성공 테스트")
    void deleteProject_ShouldDeleteProject_WhenUserIsOwner() {
        // Given
        ProjectEntityDto projectDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail("https://example.com/thumbnail.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(projectDto));
        doNothing().when(fileStorageService).deleteFile(anyString());
        doNothing().when(projectRepository).deleteById(testProjectId);

        // When
        projectService.deleteProject(testProjectId, testUserId);

        // Then
        verify(projectRepository).findById(testProjectId);
        verify(fileStorageService).deleteFile("https://example.com/thumbnail.jpg");
        verify(projectRepository).deleteById(testProjectId);
    }

    @Test
    @DisplayName("썸네일 업데이트 성공 테스트")
    void updateThumbnail_ShouldReturnUpdatedProject_WhenRequestIsValid() {
        // Given
        MockMultipartFile thumbnailFile = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                "image/jpeg",
                "thumbnail-content".getBytes()
        );

        ProjectEntityDto existingDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail("old-thumbnail-url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProjectEntityDto updatedDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .thumbnail("new-thumbnail-url")
                .createdAt(existingDto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(existingDto));
        when(fileStorageService.uploadFile(thumbnailFile, "project-thumbnails")).thenReturn("new-thumbnail-url");
        when(projectRepository.save(any(ProjectEntityDto.class))).thenReturn(updatedDto);
        doNothing().when(fileStorageService).deleteFile("old-thumbnail-url");

        // When
        ProjectResponse response = projectService.updateThumbnail(testProjectId, thumbnailFile, testUserId);

        // Then
        assertThat(response.thumbnail()).isEqualTo("new-thumbnail-url");
        verify(fileStorageService).deleteFile("old-thumbnail-url");
        verify(fileStorageService).uploadFile(thumbnailFile, "project-thumbnails");
        verify(projectRepository).save(any(ProjectEntityDto.class));
    }

    @Test
    @DisplayName("파일 업로드 실패 시 ProjectBadRequestException 발생")
    void updateThumbnail_ShouldThrowProjectBadRequestException_WhenFileUploadFails() {
        // Given
        MockMultipartFile thumbnailFile = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                "image/jpeg",
                "thumbnail-content".getBytes()
        );

        ProjectEntityDto existingDto = ProjectEntityDto.builder()
                .id(testProjectId)
                .title("Test Project")
                .description("Test Description")
                .ownerId(testUserId)
                .canvas("{\"nodes\":[], \"connections\":[]}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(existingDto));
        when(fileStorageService.uploadFile(thumbnailFile, "project-thumbnails"))
                .thenThrow(new RuntimeException("Upload failed"));

        // When & Then
        assertThatThrownBy(() -> projectService.updateThumbnail(testProjectId, thumbnailFile, testUserId))
                .isInstanceOf(ProjectBadRequestException.class)
                .hasMessageContaining("Failed to update thumbnail");

        verify(projectRepository).findById(testProjectId);
        verify(fileStorageService).uploadFile(thumbnailFile, "project-thumbnails");
    }
}
