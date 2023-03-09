package com.example.authservice.app.oauth;

import com.example.authservice.model.auth.service.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    //ServerAuthenticationSuccessHandler		 		-> interFace
    //RedirectServerAuthenticationSuccessHandler   		-> imp sample
    //DelegatingServerAuthenticationSuccessHandler 		-> imp sample
    //WebFilterChainServerAuthenticationSuccessHandler 	-> imp sample

    private final ObjectMapper objectMapper;

    private final JwtTokenProvider tokenProvider;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);


        URI url = UriComponentsBuilder.fromUriString("http://localhost:3000/login")
                .queryParam("result", true)
                .queryParam("accessToken", tokenProvider.createAccessToken(authentication))
                .build().toUri();

        return redirectStrategy.sendRedirect(exchange, url);


    }
}
