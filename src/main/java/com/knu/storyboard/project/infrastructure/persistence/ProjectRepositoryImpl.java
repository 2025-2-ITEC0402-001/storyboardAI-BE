package com.knu.storyboard.project.infrastructure.persistence;

import com.knu.storyboard.project.business.dto.ProjectEntityDto;
import com.knu.storyboard.project.business.port.ProjectRepository;
import com.knu.storyboard.project.infrastructure.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public ProjectEntityDto save(ProjectEntityDto projectEntityDto) {
        ProjectEntity entity;

        if (projectEntityDto.getId() == null) {
            entity = ProjectEntity.fromEntityDto(projectEntityDto);
        } else {
            entity = projectJpaRepository.findById(projectEntityDto.getId())
                    .map(existingEntity -> {
                        existingEntity.update(projectEntityDto);
                        return existingEntity;
                    })
                    .orElse(ProjectEntity.fromEntityDto(projectEntityDto));
        }

        ProjectEntity savedEntity = projectJpaRepository.save(entity);
        return savedEntity.toEntityDto();
    }

    @Override
    public Optional<ProjectEntityDto> findById(UUID id) {
        return projectJpaRepository.findById(id)
                .map(ProjectEntity::toEntityDto);
    }

    @Override
    public List<ProjectEntityDto> findByOwnerId(UUID ownerId) {
        return projectJpaRepository.findByOwnerId(ownerId)
                .stream()
                .map(ProjectEntity::toEntityDto)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        projectJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return projectJpaRepository.existsById(id);
    }
}
