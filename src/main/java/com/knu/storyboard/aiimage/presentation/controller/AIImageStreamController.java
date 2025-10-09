package com.knu.storyboard.aiimage.presentation.controller;

import com.knu.storyboard.aiimage.business.service.ImageGenerationTaskManager;
import com.knu.storyboard.aiimage.presentation.api.AIImageStreamApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AIImageStreamController implements AIImageStreamApi {

    private final ImageGenerationTaskManager taskManager;

    @Override
    public SseEmitter streamImageGeneration(String taskId) {
        log.info("SSE 스트림 요청: taskId={}", taskId);
        return taskManager.createSseEmitter(taskId);
    }

    @Override
    public ResponseEntity<String> getStreamStatus() {
        int activeConnections = taskManager.getActiveConnectionCount();
        String response = String.format("{\"activeConnections\": %d}", activeConnections);
        return ResponseEntity.ok(response);
    }
}
