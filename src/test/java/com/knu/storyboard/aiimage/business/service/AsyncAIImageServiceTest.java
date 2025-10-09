package com.knu.storyboard.aiimage.business.service;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.business.port.AIImageService;
import com.knu.storyboard.aiimage.business.dto.ImageGenerationTaskResponse;
import com.knu.storyboard.aiimage.exception.ImageProcessingException;
import com.knu.storyboard.config.TestEnvironmentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestEnvironmentConfig.class})
class AsyncAIImageServiceTest {

    @Mock
    private AIImageService aiImageService;

    @Mock
    private ImageGenerationTaskManager taskManager;

    @Mock
    private S3Client s3Client;

    private AsyncAIImageService asyncAIImageService;

    @BeforeEach
    void setUp() {
        asyncAIImageService = new AsyncAIImageService(aiImageService, taskManager, s3Client);
        // bucket name을 테스트용으로 설정
        // 리플렉션을 사용하여 private 필드 설정
        try {
            var field = AsyncAIImageService.class.getDeclaredField("bucketName");
            field.setAccessible(true);
            field.set(asyncAIImageService, "test-bucket");
        } catch (Exception e) {
            // 테스트 환경에서는 무시
        }
    }

    @Test
    @DisplayName("비동기 스토리보드 이미지 생성 요청 테스트")
    void generateStoryboardImageAsync_ShouldReturnTaskResponse() {
        // Given
        GenerateImageRequest request = new GenerateImageRequest(
                "A beautiful landscape",
                1024,
                1536,
                3.5,
                20,
                42
        );
        String expectedTaskId = "test-task-id";

        when(taskManager.generateTaskId()).thenReturn(expectedTaskId);
        doNothing().when(taskManager).executeAsync(any(Runnable.class));

        // When
        ImageGenerationTaskResponse response = asyncAIImageService.generateStoryboardImageAsync(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.taskId()).isEqualTo(expectedTaskId);
        verify(taskManager).generateTaskId();
        verify(taskManager).executeAsync(any(Runnable.class));
    }

    @Test
    @DisplayName("비동기 스토리보드 이미지 수정 요청 테스트")
    void reviseStoryboardImageAsync_ShouldReturnTaskResponse() {
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
        String expectedTaskId = "test-task-id-2";

        when(taskManager.generateTaskId()).thenReturn(expectedTaskId);
        doNothing().when(taskManager).executeAsync(any(Runnable.class));

        // When
        ImageGenerationTaskResponse response = asyncAIImageService.reviseStoryboardImageAsync(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.taskId()).isEqualTo(expectedTaskId);
        verify(taskManager).generateTaskId();
        verify(taskManager).executeAsync(any(Runnable.class));
    }

    @Test
    @DisplayName("S3 업로드 실패 시 ImageProcessingException 발생 테스트")
    void uploadImageToS3_ShouldThrowImageProcessingException_WhenS3Fails() {
        // Given
        byte[] imageData = "test-image-data".getBytes();
        String fileName = "test-image.png";
        String directory = "test-directory";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // When & Then
        assertThatThrownBy(() -> {
            // private 메소드를 테스트하기 위해 리플렉션 사용
            try {
                var method = AsyncAIImageService.class.getDeclaredMethod("uploadImageToS3", byte[].class, String.class, String.class);
                method.setAccessible(true);
                method.invoke(asyncAIImageService, imageData, fileName, directory);
            } catch (Exception e) {
                if (e.getCause() instanceof ImageProcessingException) {
                    throw (ImageProcessingException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        }).isInstanceOf(ImageProcessingException.class)
                .hasMessageContaining("S3 업로드 실패");
    }

    @Test
    @DisplayName("S3 업로드 성공 테스트")
    void uploadImageToS3_ShouldReturnImageUrl_WhenUploadSuccessful() {
        // Given
        byte[] imageData = "test-image-data".getBytes();
        String fileName = "test-image.png";
        String directory = "test-directory";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When & Then
        assertThatNoException().isThrownBy(() -> {
            try {
                var method = AsyncAIImageService.class.getDeclaredMethod("uploadImageToS3", byte[].class, String.class, String.class);
                method.setAccessible(true);
                String result = (String) method.invoke(asyncAIImageService, imageData, fileName, directory);
                assertThat(result).contains("test-bucket");
                assertThat(result).contains(fileName);
            } catch (Exception e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
