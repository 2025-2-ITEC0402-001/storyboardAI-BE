package com.knu.storyboard.auth.infrastructure.persistence;

import com.knu.storyboard.auth.domain.OAuthProvider;
import com.knu.storyboard.auth.infrastructure.entity.OAuthMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthMappingJpaRepository extends JpaRepository<OAuthMappingEntity, UUID> {

    Optional<OAuthMappingEntity> findBySocialUserIdAndProvider(String socialUserId,
                                                               OAuthProvider provider);

    Optional<OAuthMappingEntity> findByIdAndProvider(UUID id, OAuthProvider provider);

    void deleteByUserId(UUID userId);
}
