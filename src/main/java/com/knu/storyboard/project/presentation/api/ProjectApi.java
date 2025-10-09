package com.knu.storyboard.project.presentation.api;

import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.project.business.dto.CreateProjectRequest;
import com.knu.storyboard.project.business.dto.ProjectResponse;
import com.knu.storyboard.project.business.dto.UpdateCanvasRequest;
import com.knu.storyboard.project.business.dto.UpdateProjectRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "프로젝트", description = "스토리보드 프로젝트 관련 API")
@RequestMapping("/api/projects")
public interface ProjectApi {

    @PostMapping
    @Operation(summary = "프로젝트 생성", description = "새로운 스토리보드 프로젝트를 생성합니다.")
    ResponseEntity<ProjectResponse> createProject(
            @RequestBody CreateProjectRequest request,
            @Parameter(hidden = true) AuthUser authUser
    );

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 조회", description = "특정 프로젝트의 상세 정보를 조회합니다.")
    ResponseEntity<ProjectResponse> getProject(
            @PathVariable UUID projectId,
            @Parameter(hidden = true) AuthUser authUser
    );

    @GetMapping
    @Operation(summary = "내 프로젝트 목록 조회", description = "로그인한 사용자의 모든 프로젝트 목록을 조회합니다.")
    ResponseEntity<List<ProjectResponse>> getMyProjects(
            @Parameter(hidden = true) AuthUser authUser
    );

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 수정", description = "프로젝트 제목과 설명을 수정합니다.")
    ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID projectId,
            @RequestBody UpdateProjectRequest request,
            @Parameter(hidden = true) AuthUser authUser
    );

    @PatchMapping("/{projectId}/canvas")
    @Operation(summary = "프로젝트 캔버스 업데이트", description = "JsonPatch를 사용하여 프로젝트 캔버스를 업데이트합니다.")
    ResponseEntity<ProjectResponse> updateCanvas(
            @PathVariable UUID projectId,
            @RequestBody UpdateCanvasRequest request,
            @Parameter(hidden = true) AuthUser authUser
    );

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    ResponseEntity<Void> deleteProject(
            @PathVariable UUID projectId,
            @Parameter(hidden = true) AuthUser authUser
    );

    @PutMapping(value = "/{projectId}/thumbnail", consumes = "multipart/form-data")
    @Operation(summary = "프로젝트 썸네일 업데이트", description = "프로젝트의 썸네일 이미지를 업데이트합니다.")
    ResponseEntity<ProjectResponse> updateThumbnail(
            @PathVariable UUID projectId,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @Parameter(hidden = true) AuthUser authUser
    );
}
