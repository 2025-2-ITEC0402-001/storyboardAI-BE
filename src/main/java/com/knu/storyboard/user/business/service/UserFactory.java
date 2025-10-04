package com.knu.storyboard.user.business.service;

import com.knu.storyboard.user.domain.User;
import com.knu.storyboard.user.domain.UserDomain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFactory {

    public static User create(UUID id, String email, String nickname, String status) {
        return UserDomain.create(id, email, nickname, status);
    }

    public static String generateNicknameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
        return email.substring(0, email.indexOf("@"));
    }
}
