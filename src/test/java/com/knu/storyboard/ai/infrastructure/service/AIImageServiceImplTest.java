package com.knu.storyboard.ai.infrastructure.service;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.exception.AIBadRequestException;
import com.knu.storyboard.ai.exception.AIServiceException;
import com.knu.storyboard.config.TestEnvironmentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TestEnvironmentConfig.class})
class AIImageServiceImplTest {

    @Mock
    private WebClient generateWebClient;

    @Mock
    private WebClient reviseWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AIImageServiceImpl aiImageService;

    @BeforeEach
    void setUp() {
        aiImageService = new AIImageServiceImpl(generateWebClient, reviseWebClient);
    }

    @Test
    @DisplayName("스토리보드 이미지 생성 성공 테스트")
    void generateStoryboardImage_ShouldReturnImageBytes_WhenRequestIsValid() {
        // Given
        GenerateImageRequest request = new GenerateImageRequest(
                "A beautiful landscape with mountains",
                1024,
                1536,
                3.5,
                20,
                42
        );
        byte[] expectedImageData = "test-image-data".getBytes();

        when(generateWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(expectedImageData));

        // When
        byte[] result = aiImageService.generateStoryboardImage(request);

        // Then
        assertThat(result).isEqualTo(expectedImageData);
        verify(generateWebClient).post();
        verify(requestBodySpec).bodyValue(request);
    }

    @Test
    @DisplayName("스토리보드 이미지 생성 실패 시 AIServiceException 발생")
    void generateStoryboardImage_ShouldThrowAIServiceException_WhenWebClientFails() {
        // Given
        GenerateImageRequest request = new GenerateImageRequest(
                "A beautiful landscape",
                1024,
                1536,
                3.5,
                20,
                42
        );

        when(generateWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        // When & Then
        assertThatThrownBy(() -> aiImageService.generateStoryboardImage(request))
                .isInstanceOf(AIServiceException.class)
                .hasMessageContaining("Failed to generate image");
    }

    @Test
    @DisplayName("스토리보드 이미지 수정 성공 테스트")
    void reviseStoryboardImage_ShouldReturnImageBytes_WhenRequestIsValid() throws IOException {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        ReviseImageRequest request = new ReviseImageRequest(
                imageFile,
                "A revised beautiful landscape",
                0.8,
                3.5,
                25,
                42
        );
        byte[] expectedImageData = "revised-image-data".getBytes();

        when(reviseWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(expectedImageData));

        // When
        byte[] result = aiImageService.reviseStoryboardImage(request);

        // Then
        assertThat(result).isEqualTo(expectedImageData);
        verify(reviseWebClient).post();
    }

    @Test
    @DisplayName("이미지 파일 읽기 실패 시 AIBadRequestException 발생")
    void reviseStoryboardImage_ShouldThrowAIBadRequestException_WhenImageFileIsCorrupted() {
        // Given
        MockMultipartFile corruptedFile = new MockMultipartFile(
                "image",
                "corrupted.jpg",
                "image/jpeg",
                new byte[0]
        ) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("File is corrupted");
            }
        };

        ReviseImageRequest request = new ReviseImageRequest(
                corruptedFile,
                "A revised landscape",
                0.8,
                3.5,
                25,
                42
        );

        // When & Then
        assertThatThrownBy(() -> aiImageService.reviseStoryboardImage(request))
                .isInstanceOf(AIBadRequestException.class)
                .hasMessageContaining("Failed to read image file");
    }

    @Test
    @DisplayName("스토리보드 이미지 수정 실패 시 AIServiceException 발생")
    void reviseStoryboardImage_ShouldThrowAIServiceException_WhenWebClientFails() throws IOException {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        ReviseImageRequest request = new ReviseImageRequest(
                imageFile,
                "A revised landscape",
                0.8,
                3.5,
                25,
                42
        );

        when(reviseWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.error(new RuntimeException("AI service error")));

        // When & Then
        assertThatThrownBy(() -> aiImageService.reviseStoryboardImage(request))
                .isInstanceOf(AIServiceException.class)
                .hasMessageContaining("Failed to revise image");
    }
}
