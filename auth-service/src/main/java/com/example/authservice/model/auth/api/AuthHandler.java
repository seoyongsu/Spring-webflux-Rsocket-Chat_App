package com.example.authservice.model.auth.api;

import com.example.authservice.model.ApiResult;
import com.example.authservice.model.auth.data.JwtAuthenticationResponse;
import com.example.authservice.model.auth.data.LoginRequest;
import com.example.authservice.model.auth.data.SignUpRequest;
import com.example.authservice.model.auth.service.JwtTokenProvider;
import com.example.authservice.model.user.domain.User;
import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component("AuthHandler")
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final ReactiveAuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

//    private final WebClient webClient;

    /**
     * POST /singup
     */
    public Mono<ServerResponse> signup(ServerRequest request){
        Mono<SignUpRequest> signupRequest = request.bodyToMono(SignUpRequest.class);
        return signupRequest.map(signup->
                        User.builder()
                                .email(signup.getEmail())
                                .name(signup.getName())
                                .password(encoder.encode( signup.getPassword()))
                                .build()
                )
                .flatMap(userService::registUser)
                .then(ServerResponse.ok().bodyValue( new ApiResult(true, "회원가입성공")) )
                .onErrorResume(e-> ServerResponse.badRequest().bodyValue( new ApiResult(false, e.getMessage())));
    }


    /**
     * POST /login
     */
    public Mono<ServerResponse> login(ServerRequest request){
        Mono<LoginRequest> req = request.bodyToMono(LoginRequest.class);
        Mono<Authentication> authentication = req.flatMap(data -> {
            return authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()) );
        });



        return authentication.map(tokenProvider::createAccessToken)
                .flatMap(token -> ServerResponse.ok().bodyValue(new JwtAuthenticationResponse(token)))
                .onErrorResume(e-> ServerResponse.status(401).bodyValue( new ApiResult(false, e.getMessage())));
    }

}
