package com.knu.storyboard.aiimage.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "AI 이미지 스트림", description = "AI 이미지 생성 진행 상황 실시간 스트림 API")
@RequestMapping("/api/ai-images")
public interface AIImageStreamApi {

    @GetMapping(value = "/stream/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "AI 이미지 생성 상태 스트림",
            description = "특정 작업 ID에 대한 AI 이미지 생성 진행 상황을 실시간으로 받습니다"
    )
    SseEmitter streamImageGeneration(
            @Parameter(description = "이미지 생성 작업 ID", required = true) @PathVariable String taskId
    );

    @GetMapping("/stream/status")
    @Operation(
            summary = "활성 스트림 상태",
            description = "현재 활성화된 SSE 연결 수를 조회합니다"
    )
    ResponseEntity<String> getStreamStatus();
}