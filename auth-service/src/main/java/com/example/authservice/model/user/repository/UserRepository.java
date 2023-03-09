package com.example.authservice.model.user.repository;

import com.example.authservice.model.user.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
