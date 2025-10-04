package com.knu.storyboard.auth.infrastructure.persistence;

import com.knu.storyboard.auth.business.dto.OAuthMappingEntityDto;
import com.knu.storyboard.auth.business.dto.OAuthTokenDto;
import com.knu.storyboard.auth.business.port.OAuthRepository;
import com.knu.storyboard.auth.domain.OAuthProvider;
import com.knu.storyboard.auth.exception.OAuthNotFoundException;
import com.knu.storyboard.auth.infrastructure.entity.OAuthMappingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OAuthRepositoryImpl implements OAuthRepository {

    private final OAuthMappingJpaRepository oAuthMappingJpaRepository;

    @Override
    public OAuthMappingEntityDto save(OAuthMappingEntityDto entityDto) {
        OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(entityDto);
        OAuthMappingEntity oAuthMappingEntity = oAuthMappingJpaRepository.save(entity);
        return oAuthMappingEntity.toEntityDto();
    }

    @Override
    public Optional<OAuthMappingEntityDto> findBySocialUserIdAndProvider(String socialUserId,
                                                                         OAuthProvider provider) {
        return oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider)
                .map(OAuthMappingEntity::toEntityDto);
    }

    @Override
    public void updateToken(UUID mappingId, OAuthTokenDto oauthTokenDto) {
        OAuthMappingEntity existingEntity = oAuthMappingJpaRepository.findById(mappingId)
                .orElseThrow(() -> new OAuthNotFoundException(
                        "업데이트할 OAuth 매핑을 찾을 수 없습니다: " + mappingId));

        existingEntity.updateFromTokenDto(oauthTokenDto);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        oAuthMappingJpaRepository.deleteByUserId(userId);
    }
}
