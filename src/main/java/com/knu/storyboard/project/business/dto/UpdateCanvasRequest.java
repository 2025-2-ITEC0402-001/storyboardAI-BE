package com.knu.storyboard.project.business.dto;

import com.github.fge.jsonpatch.JsonPatch;

public record UpdateCanvasRequest(
        JsonPatch canvas
) {
}
