package com.example.authservice.model.user.service.imp;

import com.example.authservice.model.user.domain.User;
import com.example.authservice.model.user.repository.UserRepository;
import com.example.authservice.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 등록
     */
    @Override
    public Mono<User> registUser(User user){
        return userRepository.existsByEmail(user.getEmail())
        .flatMap(exists->{
           if(exists)
               return Mono.error(new Exception("이미 사용중인 Email입니다"));
           return userRepository.save(user);
        });
    }

    /**
     * 사용자 전체 조회
     */
    @Override
    public Flux<User> findAll(){
        return userRepository.findAll();
    }

    /**
     * ObjectId로 조회
     */
    @Override
    public Mono<User> findById(String id){
        return userRepository.findById(id);
    }
    
    /**
     * username으로 조회
     */
    @Override
    public Mono<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
