package com.diary_server.service;

import com.diary_server.dto.JwtResponse;
import com.diary_server.dto.UserDto;
import com.diary_server.model.RefreshToken;
import com.diary_server.repository.RefreshTokenRepository;
import com.diary_server.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse getNewToken(UserDto userDto) {
        String accessToken = jwtTokenProvider.createToken(userDto.getId(), userDto.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(userDto.getId(), userDto.getRole());
        // refresh token DB update
        RefreshToken TokenEntity = refreshTokenRepository.findByUserId(userDto.getId())
                .map(entity -> entity.update(refreshToken, userDto.getId(), jwtTokenProvider.getExpiryDateFromToken(refreshToken)))
                .orElse(RefreshToken.builder()
                        .token(refreshToken)
                        .userId(userDto.getId())
                        .expiryDate(jwtTokenProvider.getExpiryDateFromToken(refreshToken))
                        .build()
                );
        refreshTokenRepository.save(TokenEntity);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
