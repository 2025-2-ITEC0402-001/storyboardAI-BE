package com.knu.storyboard.aiimage.business.service;

import com.knu.storyboard.aiimage.business.dto.ImageGenerationEvent;
import com.knu.storyboard.config.TestEnvironmentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(TestEnvironmentConfig.class)
class ImageGenerationTaskManagerTest {

    private ImageGenerationTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new ImageGenerationTaskManager();
    }

    @Test
    @DisplayName("작업 ID 생성 테스트")
    void generateTaskId_ShouldReturnUniqueTaskId() {
        // When
        String taskId1 = taskManager.generateTaskId();
        String taskId2 = taskManager.generateTaskId();

        // Then
        assertThat(taskId1).isNotNull();
        assertThat(taskId2).isNotNull();
        assertThat(taskId1).isNotEqualTo(taskId2);
    }

    @Test
    @DisplayName("SSE 연결 생성 테스트")
    void createSseEmitter_ShouldReturnSseEmitter() {
        // Given
        String taskId = "test-task-id";

        // When
        SseEmitter emitter = taskManager.createSseEmitter(taskId);

        // Then
        assertThat(emitter).isNotNull();
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("작업 완료 시 연결 정리 테스트")
    void completeTask_ShouldRemoveConnection() {
        // Given
        String taskId = "test-task-id";
        taskManager.createSseEmitter(taskId);
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(1);

        // When
        taskManager.completeTask(taskId);

        // Then
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이벤트 전송 테스트 - 존재하지 않는 연결")
    void sendEvent_ShouldHandleNonExistentConnection() {
        // Given
        String taskId = "non-existent-task";
        ImageGenerationEvent event = ImageGenerationEvent.pending(taskId);

        // When & Then - 예외가 발생하지 않아야 함
        assertThatNoException().isThrownBy(() -> taskManager.sendEvent(taskId, event));
    }

    @Test
    @DisplayName("활성 연결 수 조회 테스트")
    void getActiveConnectionCount_ShouldReturnCorrectCount() {
        // Given
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(0);

        // When
        taskManager.createSseEmitter("task1");
        taskManager.createSseEmitter("task2");
        taskManager.createSseEmitter("task3");

        // Then
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(3);

        // When - 하나 완료
        taskManager.completeTask("task1");

        // Then
        assertThat(taskManager.getActiveConnectionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("비동기 작업 실행 테스트")
    void executeAsync_ShouldExecuteTaskAsynchronously() {
        // Given
        Runnable mockTask = mock(Runnable.class);

        // When
        taskManager.executeAsync(mockTask);

        // Then - 작업이 비동기로 실행되므로 잠시 대기 후 검증
        try {
            Thread.sleep(100); // 비동기 실행을 위한 짧은 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(mockTask, times(1)).run();
    }
}
