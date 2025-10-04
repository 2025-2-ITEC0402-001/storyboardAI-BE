package com.knu.storyboard.user.domain;

import com.knu.storyboard.user.business.dto.UserEntityDto;

import java.util.UUID;

public interface User {
    UserEntityDto toEntityDto();

    UUID getId();

    String getEmail();

    String getNickname();

    boolean isWithdrawn();

    boolean isInactive();
}
