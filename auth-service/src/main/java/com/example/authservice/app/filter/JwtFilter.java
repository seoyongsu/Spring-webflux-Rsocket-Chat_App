package com.example.authservice.app.filter;

import com.example.authservice.model.auth.service.JwtTokenProvider;
import com.example.authservice.model.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;


@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements WebFilter {
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("JwtFilter");
        String token = resolveToken( exchange.getRequest() );
        log.info("jwt : {}", token);

        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {

            Claims claims = tokenProvider.parseClaims(token);
            String username = claims.getSubject();
//            Mono<Authentication> authentication = userService.findByUsername(username)
//                    .map(user-> new User(user.getUsername(), "",  Collections.singletonList(new SimpleGrantedAuthority("USER"))) )
//                    .map(auth-> new UsernamePasswordAuthenticationToken(auth, token, auth.getAuthorities()));


            Mono<UserDetails> user = Mono.just(new User(username, "",  Collections.singletonList(new SimpleGrantedAuthority("USER"))));
            Mono<Authentication> authentication = user.map(u -> new UsernamePasswordAuthenticationToken(u.getUsername(), token, u.getAuthorities()));
            return authentication
                    .flatMap(auth-> chain.filter(exchange).contextWrite( ReactiveSecurityContextHolder.withAuthentication(auth) ));



//            User user = new User(username, "",  Collections.singletonList(new SimpleGrantedAuthority("USER")));
//            Authentication authentication =  new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
//            return chain.filter(exchange).contextWrite( ReactiveSecurityContextHolder.withAuthentication(authentication) );

        }

        return chain.filter(exchange);
    }

    /**
     * Header에서  Bearer값으로 Token 유무 확인
     */
    private String resolveToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            return authorizationHeader.substring(7);
        }
        return null;
    }
}
