package com.knu.storyboard.project.business.port;

import com.knu.storyboard.project.business.dto.ProjectEntityDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    ProjectEntityDto save(ProjectEntityDto projectEntityDto);

    Optional<ProjectEntityDto> findById(UUID id);

    List<ProjectEntityDto> findByOwnerId(UUID ownerId);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
