package com.knu.storyboard.auth.business.port;

import com.knu.storyboard.auth.business.dto.OAuthMappingEntityDto;
import com.knu.storyboard.auth.business.dto.OAuthTokenDto;
import com.knu.storyboard.auth.domain.OAuthProvider;

import java.util.Optional;
import java.util.UUID;

public interface OAuthRepository {

    OAuthMappingEntityDto save(OAuthMappingEntityDto entityDto);

    Optional<OAuthMappingEntityDto> findBySocialUserIdAndProvider(String socialUserId, OAuthProvider provider);

    void updateToken(UUID mappingId, OAuthTokenDto oauthTokenDto);

    void deleteByUserId(UUID userId);
}
