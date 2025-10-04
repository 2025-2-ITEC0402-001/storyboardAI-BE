package com.knu.storyboard.auth.infrastructure.persistence;

import com.knu.storyboard.auth.business.dto.OAuthMappingEntityDto;
import com.knu.storyboard.auth.business.dto.OAuthTokenDto;
import com.knu.storyboard.auth.domain.OAuthProvider;
import com.knu.storyboard.auth.exception.OAuthNotFoundException;
import com.knu.storyboard.auth.infrastructure.entity.OAuthMappingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthRepositoryImplTest {

    @Mock
    private OAuthMappingJpaRepository oAuthMappingJpaRepository;

    @Mock
    private OAuthMappingEntity mockEntity;

    @InjectMocks
    private OAuthRepositoryImpl oAuthRepository;

    @Test
    @DisplayName("OAuth 매핑을 저장할 수 있다")
    void save_ShouldSaveOAuthMapping() {
        // Given
        OAuthMappingEntityDto entityDto = createMockEntityDto();
        when(oAuthMappingJpaRepository.save(any(OAuthMappingEntity.class))).thenReturn(mockEntity);
        when(mockEntity.toEntityDto()).thenReturn(entityDto);

        // When
        OAuthMappingEntityDto result = oAuthRepository.save(entityDto);

        // Then
        assertThat(result).isEqualTo(entityDto);
        verify(oAuthMappingJpaRepository, times(1)).save(any(OAuthMappingEntity.class));
    }

    @Test
    @DisplayName("소셜 사용자 ID와 프로바이더로 OAuth 매핑을 찾을 수 있다")
    void findBySocialUserIdAndProvider_ShouldReturnMapping_WhenExists() {
        // Given
        String socialUserId = "social123";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthMappingEntityDto expectedDto = createMockEntityDto();
        
        when(oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider))
                .thenReturn(Optional.of(mockEntity));
        when(mockEntity.toEntityDto()).thenReturn(expectedDto);

        // When
        Optional<OAuthMappingEntityDto> result = oAuthRepository.findBySocialUserIdAndProvider(socialUserId, provider);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedDto);
        verify(oAuthMappingJpaRepository, times(1)).findBySocialUserIdAndProvider(socialUserId, provider);
    }

    @Test
    @DisplayName("존재하지 않는 소셜 사용자 ID로 조회 시 빈 Optional을 반환한다")
    void findBySocialUserIdAndProvider_ShouldReturnEmpty_WhenNotExists() {
        // Given
        String socialUserId = "nonexistent";
        OAuthProvider provider = OAuthProvider.KAKAO;
        
        when(oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider))
                .thenReturn(Optional.empty());

        // When
        Optional<OAuthMappingEntityDto> result = oAuthRepository.findBySocialUserIdAndProvider(socialUserId, provider);

        // Then
        assertThat(result).isEmpty();
        verify(oAuthMappingJpaRepository, times(1)).findBySocialUserIdAndProvider(socialUserId, provider);
    }

    @Test
    @DisplayName("토큰을 업데이트할 수 있다")
    void updateToken_ShouldUpdateToken_WhenMappingExists() {
        // Given
        UUID mappingId = UUID.randomUUID();
        OAuthTokenDto tokenDto = mock(OAuthTokenDto.class);
        
        when(oAuthMappingJpaRepository.findById(mappingId)).thenReturn(Optional.of(mockEntity));

        // When
        oAuthRepository.updateToken(mappingId, tokenDto);

        // Then
        verify(oAuthMappingJpaRepository, times(1)).findById(mappingId);
        verify(mockEntity, times(1)).updateFromTokenDto(tokenDto);
    }

    @Test
    @DisplayName("존재하지 않는 매핑 ID로 토큰 업데이트 시 예외가 발생한다")
    void updateToken_ShouldThrowException_WhenMappingNotExists() {
        // Given
        UUID mappingId = UUID.randomUUID();
        OAuthTokenDto tokenDto = mock(OAuthTokenDto.class);
        
        when(oAuthMappingJpaRepository.findById(mappingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oAuthRepository.updateToken(mappingId, tokenDto))
                .isInstanceOf(OAuthNotFoundException.class)
                .hasMessage("업데이트할 OAuth 매핑을 찾을 수 없습니다: " + mappingId);
        
        verify(mockEntity, never()).updateFromTokenDto(any());
    }

    @Test
    @DisplayName("사용자 ID로 OAuth 매핑을 삭제할 수 있다")
    void deleteByUserId_ShouldDeleteMappings() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        oAuthRepository.deleteByUserId(userId);

        // Then
        verify(oAuthMappingJpaRepository, times(1)).deleteByUserId(userId);
    }

    private OAuthMappingEntityDto createMockEntityDto() {
        return OAuthMappingEntityDto.create(
                UUID.randomUUID(),
                "social123",
                "user@social.com",
                "Social User",
                OAuthProvider.KAKAO,
                UUID.randomUUID(),
                null,
                false
        );
    }
}