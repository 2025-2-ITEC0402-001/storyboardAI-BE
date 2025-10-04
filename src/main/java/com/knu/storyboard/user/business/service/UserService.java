package com.knu.storyboard.user.business.service;

import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.business.port.OAuthRepository;
import com.knu.storyboard.auth.business.service.OAuthLoginService;
import com.knu.storyboard.auth.domain.DeviceType;
import com.knu.storyboard.user.business.dto.DummyRequest;
import com.knu.storyboard.user.business.dto.UserEntityDto;
import com.knu.storyboard.user.business.port.UserRepository;
import com.knu.storyboard.user.domain.User;
import com.knu.storyboard.user.domain.UserStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OAuthLoginService oAuthLoginService;
    private final OAuthRepository oAuthRepository;

    @Transactional
    public JwtResponse dummyLogin(DummyRequest dummyRequest) {
        String nickname = UserFactory.generateNicknameFromEmail(dummyRequest.email());
        UserEntityDto userEntityDto = userRepository.findOptionalByEmail(dummyRequest.email())
                .map(user -> userRepository.getByEmail(dummyRequest.email()))
                .orElseGet(() -> userRepository.save(
                        dummyRequest.email(),
                        nickname,
                        UserStatus.ACTIVE.name()
                ));
        User user = userEntityDto.toDomain();

        return oAuthLoginService.generateTokensForUser(user.getId(), DeviceType.COMPUTER);
    }

    public void deleteUser(UUID userId) {
        oAuthRepository.deleteByUserId(userId);
        
        userRepository.delete(userId);
    }
}
