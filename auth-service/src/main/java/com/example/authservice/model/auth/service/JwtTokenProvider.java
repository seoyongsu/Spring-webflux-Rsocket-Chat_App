package com.example.authservice.model.auth.service;


import com.example.authservice.app.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

//    private SecretKey secretKey;
//    @PostConstruct
//    public void ini1t() {
//        String secret = Base64.getEncoder().encodeToString(jwtConfig.getKey().getBytes());
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }

    private SecretKey secretKey() {
        String secret = Base64.getEncoder().encodeToString(jwtConfig.getKey().getBytes());
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * AccessToken 생성
     */
    public String createAccessToken(Authentication authentication){

        Long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(String.valueOf( authentication.getName()) )
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList())
                )
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()))
                .signWith(secretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 파싱하기 (복호화)
     * JWT에서 Claims 가져오기
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    /**
     * JWT Validation 확인
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder().setSigningKey(secretKey()).build()
                    .parseClaimsJws(token);
            log.info("expiration date: {}", claims.getBody().getExpiration());

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }



}
