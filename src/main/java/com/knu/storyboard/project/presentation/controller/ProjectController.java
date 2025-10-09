package com.knu.storyboard.project.presentation.controller;

import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.auth.presentation.annotation.Login;
import com.knu.storyboard.auth.presentation.annotation.RequireAuth;
import com.knu.storyboard.project.business.dto.CreateProjectRequest;
import com.knu.storyboard.project.business.dto.ProjectResponse;
import com.knu.storyboard.project.business.dto.UpdateCanvasRequest;
import com.knu.storyboard.project.business.dto.UpdateProjectRequest;
import com.knu.storyboard.project.business.service.ProjectService;
import com.knu.storyboard.project.presentation.api.ProjectApi;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    @Override
    @RequireAuth
    public ResponseEntity<ProjectResponse> createProject(CreateProjectRequest request,
                                                         @Parameter(hidden = true) @Login AuthUser authUser) {
        ProjectResponse response = projectService.createProject(request, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<ProjectResponse> getProject(UUID projectId,
                                                      @Parameter(hidden = true) @Login AuthUser authUser) {
        ProjectResponse response = projectService.getProjectById(projectId, authUser.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@Parameter(hidden = true) @Login AuthUser authUser) {
        List<ProjectResponse> responses = projectService.getProjectsByOwnerId(authUser.getId());
        return ResponseEntity.ok(responses);
    }

    @Override
    @RequireAuth
    public ResponseEntity<ProjectResponse> updateProject(UUID projectId, UpdateProjectRequest request,
                                                         @Parameter(hidden = true) @Login AuthUser authUser) {
        ProjectResponse response = projectService.updateProject(projectId, request, authUser.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<ProjectResponse> updateCanvas(UUID projectId, UpdateCanvasRequest request,
                                                        @Parameter(hidden = true) @Login AuthUser authUser) {
        ProjectResponse response = projectService.updateCanvas(projectId, request, authUser.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    @RequireAuth
    public ResponseEntity<Void> deleteProject(UUID projectId,
                                              @Parameter(hidden = true) @Login AuthUser authUser) {
        projectService.deleteProject(projectId, authUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    @RequireAuth
    public ResponseEntity<ProjectResponse> updateThumbnail(UUID projectId, MultipartFile thumbnail,
                                                           @Parameter(hidden = true) @Login AuthUser authUser) {
        ProjectResponse response = projectService.updateThumbnail(projectId, thumbnail, authUser.getId());
        return ResponseEntity.ok(response);
    }
}
