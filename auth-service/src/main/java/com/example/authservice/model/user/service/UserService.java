package com.example.authservice.model.user.service;

import com.example.authservice.model.user.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> registUser(User user);
    Flux<User> findAll();
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
}
