package com.knu.storyboard.auth.business.service;

import com.knu.storyboard.auth.business.dto.*;
import com.knu.storyboard.auth.business.port.OAuthRepository;
import com.knu.storyboard.auth.business.port.OAuthService;
import com.knu.storyboard.auth.business.port.TokenRepository;
import com.knu.storyboard.auth.domain.*;
import com.knu.storyboard.auth.exception.OAuthBadRequestException;
import com.knu.storyboard.user.business.dto.UserEntityDto;
import com.knu.storyboard.user.business.port.UserRepository;
import com.knu.storyboard.user.business.service.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final Map<String, OAuthService> oAuthServices;
    private final OAuthRepository oAuthRepository;
    private final UserRepository userRepository;
    private final JwtFactory jwtFactory;
    private final TokenRepository tokenRepository;

    @Value("${OAUTH_APP_REDIRECT_URI}")
    private String appRedirectUri;

    public String getOAuthLoginUrl(String provider, String state) {
        OAuthContext context = parseOAuthContext(provider, state);

        OAuthService oAuthService = getOAuthService(context.oAuthProvider());

        if (!oAuthService.isBackendRedirect()) {
            throw new OAuthBadRequestException("이 제공자는 백엔드 리다이렉트를 지원하지 않습니다.");
        }

        return oAuthService.getRedirectUrl(context.deviceType().name());
    }

    @Transactional
    public URI handleOAuthCallback(String provider, String code, String state) {
        OAuthContext context = parseOAuthContext(provider, state);

        OAuthLoginResponse loginResponse = processOAuthLogin(context.oAuthProvider(), code,
                context.deviceType());

        StringBuilder redirectBuilder = new StringBuilder(appRedirectUri);

        String urlFragmentDelimiter = "#";

        redirectBuilder.append(urlFragmentDelimiter)
                .append("accessToken=").append(loginResponse.accessToken())
                .append("&refreshToken=").append(loginResponse.refreshToken());

        return URI.create(redirectBuilder.toString());
    }

    private OAuthContext parseOAuthContext(String provider, String state) {
        OAuthProvider oAuthProvider = OAuthProvider.fromString(provider);
        DeviceType deviceType = DeviceType.fromString(state);
        return OAuthContext.of(oAuthProvider, deviceType);
    }

    @Transactional
    public OAuthLoginResponse processOAuthLogin(OAuthProvider provider, String code, DeviceType deviceType) {
        OAuthService oAuthService = getOAuthService(provider);

        OAuthUserInfo userInfo = oAuthService.getUserInfo(code);
        String email = userInfo.getEmail();

        Optional<UserEntityDto> existingUser = userRepository.findOptionalByEmail(email);

        if (existingUser.isPresent()) {
            UserEntityDto userDto = existingUser.get();

            Optional<OAuthMappingEntityDto> existingMappingDto =
                    oAuthRepository.findBySocialUserIdAndProvider(userInfo.getSocialUserId(), provider);

            if (existingMappingDto.isPresent()) {
                OAuthToken oauthToken = userInfo.getOAuthToken();
                OAuthTokenDto oauthTokenDto = OAuthTokenDto.from(oauthToken);
                oAuthRepository.updateToken(existingMappingDto.get().getId(), oauthTokenDto);
            } else {
                OAuthToken oauthToken = userInfo.getOAuthToken();
                OAuthMapping newMapping = OAuthMapping.createForUser(
                        userDto.getId(),
                        userInfo.getSocialUserId(),
                        email,
                        userInfo.getName(),
                        provider,
                        oauthToken
                );
                oAuthRepository.save(newMapping.toDto());
            }

            JwtResponse jwtResponse = generateTokensForUser(userDto.getId(), deviceType);
            return OAuthLoginResponse.toJwt(jwtResponse.accessToken(), jwtResponse.refreshToken());
        } else {
            String nickname = UserFactory.generateNicknameFromEmail(email);
            UserEntityDto newUserDto = userRepository.save(email, nickname, "ACTIVE");

            OAuthToken oauthToken = userInfo.getOAuthToken();
            OAuthMapping newMapping = OAuthMapping.createForUser(
                    newUserDto.getId(),
                    userInfo.getSocialUserId(),
                    email,
                    userInfo.getName(),
                    provider,
                    oauthToken
            );
            oAuthRepository.save(newMapping.toDto());

            JwtResponse jwtResponse = generateTokensForUser(newUserDto.getId(), deviceType);
            return OAuthLoginResponse.toJwt(jwtResponse.accessToken(), jwtResponse.refreshToken());
        }
    }

    public JwtResponse generateTokensForUser(UUID userId, DeviceType deviceType) {
        UserEntityDto userEntityDto = userRepository.getById(userId);
        UserFactory.create(userEntityDto.getId(), userEntityDto.getEmail(),
                userEntityDto.getNickname(), userEntityDto.getStatus());

        Token accessToken = jwtFactory.createAccessToken(userId);
        Token refreshToken = jwtFactory.createRefreshToken(userId);

        TokenDTO refreshTokenDTO = refreshToken.toTokenDTO();

        tokenRepository.saveToken(userId, deviceType.name(), refreshTokenDTO);

        return new JwtResponse(accessToken.getValue(), refreshToken.getValue());
    }

    private OAuthService getOAuthService(OAuthProvider provider) {
        String serviceBeanName = provider.name().toLowerCase() + "OAuthService";
        OAuthService service = oAuthServices.get(serviceBeanName);

        if (service == null) {
            throw new OAuthBadRequestException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }

        return service;
    }
}
