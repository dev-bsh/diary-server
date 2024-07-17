package com.diary_server.config.auth;

import com.diary_server.config.auth.dto.OAuth2UserInfo;
import com.diary_server.config.auth.dto.OAuth2UserInfoFactory;
import com.diary_server.model.Role;
import com.diary_server.model.User;
import com.diary_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // registrationId별 사용자 정보 처리
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (userInfo.getProviderId() == null) {
            throw new OAuth2AuthenticationException(registrationId + " OAuth2 user provider id not found");
        }

        // 사용자 정보 DB 저장
        saveOrUpdate(userInfo);

        // Authentication 객체에 들어갈 user attributes 생성
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("providerId", userInfo.getProviderId());
        userAttributes.put("provider", userInfo.getProvider());
        userAttributes.put("name", userInfo.getName());
        userAttributes.put("email", userInfo.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(userAttributes)),
                userAttributes,
                "providerId"
        );
    }

    public void saveOrUpdate(OAuth2UserInfo userInfo) {
        User user = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .map(entity -> entity.update(userInfo.getName(), userInfo.getEmail()))
                .orElse(User.builder()
                        .username(userInfo.getName())
                        .email(userInfo.getEmail())
                        .provider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .role(Role.USER)
                        .build()
                );
        userRepository.save(user);
    }
}
