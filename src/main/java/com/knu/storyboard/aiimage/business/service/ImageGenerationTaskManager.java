package com.knu.storyboard.aiimage.business.service;

import com.knu.storyboard.aiimage.business.dto.ImageGenerationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ImageGenerationTaskManager {

    private final Map<String, SseEmitter> activeConnections = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    public SseEmitter createSseEmitter(String taskId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5분 타임아웃

        emitter.onCompletion(() -> {
            activeConnections.remove(taskId);
            log.info("SSE 연결 완료: taskId={}", taskId);
        });

        emitter.onTimeout(() -> {
            activeConnections.remove(taskId);
            log.warn("SSE 연결 타임아웃: taskId={}", taskId);
        });

        emitter.onError(ex -> {
            activeConnections.remove(taskId);
            log.error("SSE 연결 오류: taskId={}", taskId, ex);
        });

        activeConnections.put(taskId, emitter);
        log.info("SSE 연결 생성: taskId={}", taskId);

        return emitter;
    }

    public void sendEvent(String taskId, ImageGenerationEvent event) {
        SseEmitter emitter = activeConnections.get(taskId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("image-generation")
                        .data(event));
                log.debug("SSE 이벤트 전송 성공: taskId={}, status={}", taskId, event.status());
            } catch (IOException e) {
                log.error("SSE 이벤트 전송 실패: taskId={}", taskId, e);
                activeConnections.remove(taskId);
                emitter.completeWithError(e);
            }
        } else {
            log.warn("활성 SSE 연결을 찾을 수 없음: taskId={}", taskId);
        }
    }

    public void completeTask(String taskId) {
        SseEmitter emitter = activeConnections.remove(taskId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("SSE 작업 완료: taskId={}", taskId);
            } catch (Exception e) {
                log.error("SSE 완료 처리 오류: taskId={}", taskId, e);
            }
        }
    }

    public void executeAsync(Runnable task) {
        executor.submit(task);
    }

    public int getActiveConnectionCount() {
        return activeConnections.size();
    }
}
