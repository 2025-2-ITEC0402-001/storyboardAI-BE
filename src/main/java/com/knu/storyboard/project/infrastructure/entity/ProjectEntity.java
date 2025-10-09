package com.knu.storyboard.project.infrastructure.entity;

import com.knu.storyboard.project.business.dto.ProjectEntityDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PROJECTS")
public class ProjectEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "char(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "char(36)", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID ownerId;

    @Column(columnDefinition = "LONGTEXT")
    private String canvas;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static ProjectEntity fromEntityDto(ProjectEntityDto dto) {
        return ProjectEntity.builder()
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

    public void update(ProjectEntityDto dto) {
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getCanvas() != null) {
            this.canvas = dto.getCanvas();
        }
        if (dto.getThumbnail() != null) {
            this.thumbnail = dto.getThumbnail();
        }
        this.updatedAt = LocalDateTime.now();
    }
}