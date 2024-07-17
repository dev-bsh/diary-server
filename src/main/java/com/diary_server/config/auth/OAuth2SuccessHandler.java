package com.diary_server.config.auth;

import com.diary_server.model.RefreshToken;
import com.diary_server.model.User;
import com.diary_server.repository.RefreshTokenRepository;
import com.diary_server.repository.UserRepository;
import com.diary_server.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // CustomOAuth2UserService의 loadUser 메서드에서 반환한 객체 사용
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = (String) oAuth2User.getAttributes().get("provider");
        String providerId = (String) oAuth2User.getAttributes().get("providerId");

        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(() -> new IllegalStateException("User not found"));

        // jwt 토큰 생성
        String token = tokenProvider.createToken(user.getId(), user.getRole());
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), user.getRole());

        // refresh token 갱신
        refreshTokenSaveOrUpdate(refreshToken, user.getId());

        // redirect url에 jwt 추가
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(frontendUrl + "/success")
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    public void refreshTokenSaveOrUpdate(String token, Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(token, userId, tokenProvider.getExpiryDateFromToken(token)))
                .orElse(RefreshToken.builder()
                        .token(token)
                        .userId(userId)
                        .expiryDate(tokenProvider.getExpiryDateFromToken(token))
                        .build()
                );
        refreshTokenRepository.save(refreshToken);
    }


}
