package com.example.authservice.model.auth.service;

import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements ReactiveUserDetailsService {

    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByEmail(username).map(user->
           new User(user.getEmail(), user.getPassword(),  Collections.singletonList(new SimpleGrantedAuthority("USER")))
        );
    }
}
