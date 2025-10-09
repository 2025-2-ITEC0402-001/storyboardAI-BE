package com.knu.storyboard.aiimage.business.service;

import com.knu.storyboard.ai.business.dto.GenerateImageRequest;
import com.knu.storyboard.ai.business.dto.ReviseImageRequest;
import com.knu.storyboard.ai.business.port.AIImageService;
import com.knu.storyboard.aiimage.business.dto.ImageGenerationEvent;
import com.knu.storyboard.aiimage.business.dto.ImageGenerationTaskResponse;
import com.knu.storyboard.aiimage.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAIImageService {

    private final AIImageService aiImageService;
    private final ImageGenerationTaskManager taskManager;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public ImageGenerationTaskResponse generateStoryboardImageAsync(GenerateImageRequest request) {
        String taskId = taskManager.generateTaskId();

        taskManager.executeAsync(() -> {
            try {
                taskManager.sendEvent(taskId, ImageGenerationEvent.pending(taskId));

                taskManager.sendEvent(taskId, ImageGenerationEvent.inProgress(taskId, "AI 모델이 이미지를 생성중입니다", 30));

                byte[] imageData = aiImageService.generateStoryboardImage(request);

                taskManager.sendEvent(taskId, ImageGenerationEvent.uploading(taskId));

                String imageUrl = uploadImageToS3(imageData, "generated-" + taskId + ".png", "ai-generated-images");

                taskManager.sendEvent(taskId, ImageGenerationEvent.completed(taskId, imageUrl));

            } catch (Exception e) {
                log.error("이미지 생성 실패: taskId={}", taskId, e);
                taskManager.sendEvent(taskId, ImageGenerationEvent.failed(taskId, e.getMessage()));
            } finally {
                taskManager.completeTask(taskId);
            }
        });

        return ImageGenerationTaskResponse.of(taskId);
    }

    public ImageGenerationTaskResponse reviseStoryboardImageAsync(ReviseImageRequest request) {
        String taskId = taskManager.generateTaskId();

        taskManager.executeAsync(() -> {
            try {
                taskManager.sendEvent(taskId, ImageGenerationEvent.pending(taskId));

                taskManager.sendEvent(taskId, ImageGenerationEvent.inProgress(taskId, "AI 모델이 이미지를 수정중입니다", 30));

                byte[] imageData = aiImageService.reviseStoryboardImage(request);

                taskManager.sendEvent(taskId, ImageGenerationEvent.uploading(taskId));

                String imageUrl = uploadImageToS3(imageData, "revised-" + taskId + ".png", "ai-generated-images");

                taskManager.sendEvent(taskId, ImageGenerationEvent.completed(taskId, imageUrl));

            } catch (Exception e) {
                log.error("이미지 수정 실패: taskId={}", taskId, e);
                taskManager.sendEvent(taskId, ImageGenerationEvent.failed(taskId, e.getMessage()));
            } finally {
                taskManager.completeTask(taskId);
            }
        });

        return ImageGenerationTaskResponse.of(taskId);
    }

    private String uploadImageToS3(byte[] imageData, String fileName, String directory) {
        try {
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String key = String.format("%s/%s/%s", directory, dateDir, fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("image/png")
                    .contentLength((long) imageData.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageData));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            log.info("S3 이미지 업로드 완료: {} -> {}", fileName, fileUrl);

            return fileUrl;
        } catch (Exception e) {
            log.error("S3 이미지 업로드 실패: {}", fileName, e);
            throw new ImageProcessingException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }
}
