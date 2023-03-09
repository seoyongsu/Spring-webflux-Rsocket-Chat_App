package com.example.authservice.model.user.api;

import com.example.authservice.model.user.client.ChatServiceClient;
import com.example.authservice.model.user.data.UserData;
import com.example.authservice.model.user.domain.User;
import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component("UserHandler")
@RequiredArgsConstructor
@Slf4j
public class UserHandler {

    private final UserService userService;
    private final ChatServiceClient client;

    /**
     * GET /users/{username}
     */
    public Mono<ServerResponse> findUser(ServerRequest request){
        log.error("findUser");
        String username = request.pathVariable("username");
        return userService.findByEmail(username)
                .flatMap(user->ServerResponse.ok().bodyValue(user));
    }

    /**
     * GET /users
     */
    public Mono<ServerResponse> findAll(ServerRequest request){
        log.error("findAll");
        Flux<User> users = userService.findAll();
        return ServerResponse.ok().body(users, User.class);
    }

    /**
     * GET /users/datas
     */
    public Mono<ServerResponse> findAllUserDatas(ServerRequest request){
        log.info("findAllUserDatas");

        Mono<String> username = request.principal().map(Principal::getName);

        Flux<UserData> userDatas = username.flatMapMany(name ->
            userService.findAll()
            .filter(users-> !users.getEmail().equals(name))
            .flatMap(user -> {
                return client.connectByUnsername(user.getEmail())
                        .map(status -> convertTo(user, status));
            })
        );

        return ServerResponse.ok()
                .body(userDatas, UserData.class);
    }

    /**
     * GET /users/me
     */
    public Mono<ServerResponse> getCurrentUser(ServerRequest request){
        log.info("getCurrentUser");
        return request.principal().flatMap(principal -> {
                    return userService.findByEmail(principal.getName())
                            .flatMap(user->ServerResponse.ok()
                                    .bodyValue( convertTo(user, false) ));
                });
    }

    /**
     * GET /users/data/{username}
     */
    public Mono<ServerResponse> getUserData(ServerRequest request){
        String username = request.pathVariable("username");
        return userService.findByEmail(username)
                .flatMap(user-> ServerResponse.ok().bodyValue( convertTo(user, false) ));
    }

    private UserData convertTo(User user, boolean connectStatus){
        return UserData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .connectStatus(connectStatus)
                .build();
    }


}
