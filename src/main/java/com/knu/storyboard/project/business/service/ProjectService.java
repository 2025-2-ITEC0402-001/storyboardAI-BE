package com.knu.storyboard.project.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.storyboard.common.file.FileStorageService;
import com.knu.storyboard.project.business.dto.*;
import com.knu.storyboard.project.business.port.ProjectRepository;
import com.knu.storyboard.project.domain.StoryboardProject;
import com.knu.storyboard.project.exception.ProjectBadRequestException;
import com.knu.storyboard.project.exception.ProjectForbiddenException;
import com.knu.storyboard.project.exception.ProjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    public ProjectResponse createProject(CreateProjectRequest request, UUID ownerId) {
        StoryboardProject project = StoryboardProject.create(
                request.title(),
                request.description(),
                ownerId
        );

        ProjectEntityDto savedDto = projectRepository.save(project.toEntityDto());
        return ProjectResponse.from(StoryboardProject.fromDto(savedDto));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId, UUID userId) {
        ProjectEntityDto projectDto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        StoryboardProject project = StoryboardProject.fromDto(projectDto);

        if (!project.isOwnedBy(userId)) {
            throw new ProjectForbiddenException("Access denied: You are not the owner of this project");
        }

        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByOwnerId(UUID ownerId) {
        List<ProjectEntityDto> projectDtos = projectRepository.findByOwnerId(ownerId);

        return projectDtos.stream()
                .map(StoryboardProject::fromDto)
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID userId) {
        ProjectEntityDto projectDto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        StoryboardProject project = StoryboardProject.fromDto(projectDto);

        if (!project.isOwnedBy(userId)) {
            throw new ProjectForbiddenException("Access denied: You are not the owner of this project");
        }

        StoryboardProject updatedProject = project
                .updateTitle(request.title())
                .updateDescription(request.description());

        ProjectEntityDto savedDto = projectRepository.save(updatedProject.toEntityDto());
        return ProjectResponse.from(StoryboardProject.fromDto(savedDto));
    }

    public ProjectResponse updateCanvas(UUID projectId, UpdateCanvasRequest request, UUID userId) {
        ProjectEntityDto projectDto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        StoryboardProject project = StoryboardProject.fromDto(projectDto);

        if (!project.isOwnedBy(userId)) {
            throw new ProjectForbiddenException("Access denied: You are not the owner of this project");
        }

        try {
            ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode currentCanvas = mapper.readTree(project.getCanvas());
            JsonNode updatedCanvas = request.canvas().apply(currentCanvas);

            StoryboardProject updatedProject = project.updateCanvas(updatedCanvas.toString());
            ProjectEntityDto savedDto = projectRepository.save(updatedProject.toEntityDto());
            return ProjectResponse.from(StoryboardProject.fromDto(savedDto));
        } catch (Exception e) {
            throw new ProjectBadRequestException("Failed to apply canvas patch: " + e.getMessage());
        }
    }

    public void deleteProject(UUID projectId, UUID userId) {
        ProjectEntityDto projectDto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        StoryboardProject project = StoryboardProject.fromDto(projectDto);

        if (!project.isOwnedBy(userId)) {
            throw new ProjectForbiddenException("Access denied: You are not the owner of this project");
        }

        if (project.getThumbnail() != null && !project.getThumbnail().isEmpty()) {
            fileStorageService.deleteFile(project.getThumbnail());
        }

        projectRepository.deleteById(projectId);
    }

    public ProjectResponse updateThumbnail(UUID projectId, MultipartFile thumbnailFile, UUID userId) {
        ProjectEntityDto projectDto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        StoryboardProject project = StoryboardProject.fromDto(projectDto);

        if (!project.isOwnedBy(userId)) {
            throw new ProjectForbiddenException("Access denied: You are not the owner of this project");
        }

        try {
            if (project.getThumbnail() != null && !project.getThumbnail().isEmpty()) {
                fileStorageService.deleteFile(project.getThumbnail());
            }

            String thumbnailUrl = fileStorageService.uploadFile(thumbnailFile, "project-thumbnails");

            StoryboardProject updatedProject = project.updateThumbnail(thumbnailUrl);
            ProjectEntityDto savedDto = projectRepository.save(updatedProject.toEntityDto());
            return ProjectResponse.from(StoryboardProject.fromDto(savedDto));
        } catch (Exception e) {
            throw new ProjectBadRequestException("Failed to update thumbnail: " + e.getMessage());
        }
    }
}
