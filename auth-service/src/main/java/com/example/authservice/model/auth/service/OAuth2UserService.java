package com.example.authservice.model.auth.service;

import com.example.authservice.app.oauth.OAuth2Attributes;
import com.example.authservice.model.user.domain.User;
import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();
        String providerType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        return delegate.loadUser(userRequest).flatMap(oAuth2User -> {
            OAuth2Attributes userInfo = OAuth2Attributes.of(providerType, oAuth2User.getAttributes());


            /**
             * OAtuh2 Provider의 필수데이터 확인
             * 서드파티 validation Check
             */
            if(userInfo.getEmail()==null) {
                OAuth2Error error = new OAuth2Error("providerNoData");
                return Mono.error(new Exception("OAUTH2공급자" + providerType +"에서  Email정보를 찾을수 없습니다."));
            }

            return userService.findByEmail(userInfo.getEmail())
                    .switchIfEmpty(Mono.defer(()->{
                        User user = User.builder()
                                .email(userInfo.getEmail())
                                .name(userInfo.getName())
                                .providerType(userInfo.getProviderType())
                                .build();
                        return userService.registUser(user);
                    }))
                    .map(user-> {
                        //Spring Security OAuth2User객체 생성시 username을 e메일로 사용하기 위한 임시 attribute
                        Map<String, Object> tempAttributes = new HashMap<>();
                        tempAttributes.put("email", user.getEmail());
                        return new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("USER")), tempAttributes, "email");
                    });
        });
    }
}
