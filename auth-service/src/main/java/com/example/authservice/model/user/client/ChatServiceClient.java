package com.example.authservice.model.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Boolean> connectByUnsername(String username){
        return webClientBuilder.build()
                .get()
                .uri("localhost:8080/connect/"+username)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(throwable->{
                    return Mono.just(false);
                });
    }

    public Flux<Object> connectByAll(){
        return webClientBuilder.build()
                        .get()
                        .uri("localhost:8080/connectAll")
                        .retrieve()
                        .bodyToFlux(Object.class);
    }


}
