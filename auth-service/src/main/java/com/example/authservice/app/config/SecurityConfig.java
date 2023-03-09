package com.example.authservice.app.config;

import com.example.authservice.app.filter.JwtFilter;
import com.example.authservice.app.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.authservice.model.auth.service.JwtTokenProvider;
import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;


    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    /**
     * Spring Security PasswordEncoder Bean 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * ReactiveAuthenticationManager Bean 등록
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    /**
     * Webflux SecurityWebFilterChain Bean
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.cors().and().csrf().disable();

        http.authorizeExchange(exchange-> exchange
                .pathMatchers(HttpMethod.POST,"/login").permitAll()
                .pathMatchers(HttpMethod.POST, "/signup").permitAll()
                .pathMatchers("/oauth2/**").permitAll()
//                .pathMatchers("/users/**").permitAll()
                .anyExchange().authenticated()
        );

        http.oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(oAuth2AuthenticationSuccessHandler)
//                .authenticationFailureHandler(oAuth2AuthenticationFailureHandler)
        );


        // 세션정책 끄기
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.addFilterAt(new JwtFilter(tokenProvider,userService), SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();
    }

}
