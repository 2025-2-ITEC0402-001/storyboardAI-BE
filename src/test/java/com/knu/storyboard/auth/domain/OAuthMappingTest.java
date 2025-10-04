package com.knu.storyboard.auth.domain;

import com.knu.storyboard.auth.business.dto.OAuthMappingEntityDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthMappingTest {

    @Test
    @DisplayName("임시 OAuth 매핑을 생성할 수 있다")
    void createTemporary_ShouldCreateTemporaryOAuthMapping() {
        // Given
        String providerId = "social123";
        String providerEmail = "user@social.com";
        String providerName = "Social User";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.create(provider, "access-token", "refresh-token", 3600L);

        // When
        OAuthMapping mapping = OAuthMapping.createTemporary(providerId, providerEmail, providerName, provider, oauthToken);

        // Then
        assertThat(mapping.getSocialUserId()).isEqualTo(providerId);
        assertThat(mapping.getSocialUserEmail()).isEqualTo(providerEmail);
        assertThat(mapping.getSocialUserName()).isEqualTo(providerName);
        assertThat(mapping.getProvider()).isEqualTo(provider);
        assertThat(mapping.getOauthToken()).isEqualTo(oauthToken);
        assertThat(mapping.isTemporary()).isTrue();
        assertThat(mapping.getUserId()).isNull();
    }

    @Test
    @DisplayName("사용자와 연결된 OAuth 매핑을 생성할 수 있다")
    void createForUser_ShouldCreateOAuthMappingForUser() {
        // Given
        UUID userId = UUID.randomUUID();
        String providerId = "social123";
        String providerEmail = "user@social.com";
        String providerName = "Social User";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.create(provider, "access-token", "refresh-token", 3600L);

        // When
        OAuthMapping mapping = OAuthMapping.createForUser(userId, providerId, providerEmail, providerName, provider, oauthToken);

        // Then
        assertThat(mapping.getSocialUserId()).isEqualTo(providerId);
        assertThat(mapping.getSocialUserEmail()).isEqualTo(providerEmail);
        assertThat(mapping.getSocialUserName()).isEqualTo(providerName);
        assertThat(mapping.getProvider()).isEqualTo(provider);
        assertThat(mapping.getOauthToken()).isEqualTo(oauthToken);
        assertThat(mapping.getUserId()).isEqualTo(userId);
        assertThat(mapping.isTemporary()).isFalse();
    }

    @Test
    @DisplayName("임시 매핑을 사용자와 연결할 수 있다")
    void linkToUser_ShouldLinkTemporaryMappingToUser() {
        // Given
        UUID mappingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String providerId = "social123";
        String providerEmail = "user@social.com";
        String providerName = "Social User";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.create(provider, "access-token", "refresh-token", 3600L);

        OAuthMapping temporaryMapping = OAuthMapping.builder()
                .id(mappingId)
                .socialUserId(providerId)
                .socialUserEmail(providerEmail)
                .socialUserName(providerName)
                .provider(provider)
                .oauthToken(oauthToken)
                .temporary(true)
                .build();

        // When
        OAuthMapping linkedMapping = temporaryMapping.linkToUser(userId);

        // Then
        assertThat(linkedMapping.getId()).isEqualTo(mappingId);
        assertThat(linkedMapping.getSocialUserId()).isEqualTo(providerId);
        assertThat(linkedMapping.getUserId()).isEqualTo(userId);
        assertThat(linkedMapping.isTemporary()).isFalse();
    }

    @Test
    @DisplayName("OAuthMappingEntityDto로 변환할 수 있다")
    void toDto_ShouldReturnCorrectDto() {
        // Given
        UUID mappingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String providerId = "social123";
        String providerEmail = "user@social.com";
        String providerName = "Social User";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.create(provider, "access-token", "refresh-token", 3600L);

        OAuthMapping mapping = OAuthMapping.builder()
                .id(mappingId)
                .socialUserId(providerId)
                .socialUserEmail(providerEmail)
                .socialUserName(providerName)
                .provider(provider)
                .userId(userId)
                .oauthToken(oauthToken)
                .temporary(false)
                .build();

        // When
        OAuthMappingEntityDto dto = mapping.toDto();

        // Then
        assertThat(dto.getId()).isEqualTo(mappingId);
        assertThat(dto.getSocialUserId()).isEqualTo(providerId);
        assertThat(dto.getSocialUserEmail()).isEqualTo(providerEmail);
        assertThat(dto.getSocialUserName()).isEqualTo(providerName);
        assertThat(dto.getProvider()).isEqualTo(provider);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getOauthToken()).isEqualTo(oauthToken);
        assertThat(dto.isTemporary()).isFalse();
    }
}