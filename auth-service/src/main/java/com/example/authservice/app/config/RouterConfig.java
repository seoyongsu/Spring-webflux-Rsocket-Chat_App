package com.example.authservice.app.config;

import com.example.authservice.model.auth.api.AuthHandler;
import com.example.authservice.model.user.api.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    @Bean
    RouterFunction<ServerResponse> authEndPoint(AuthHandler handler) {
        return RouterFunctions
                .route( 	POST("/signup"), handler :: signup)
                .andRoute(  POST("/login"), handler::login)
                ;
    }


    @Bean
    RouterFunction<ServerResponse> userEndPoint(UserHandler handler){

        return RouterFunctions
                .route(     GET("/users")                 ,   handler::findAll)
                .andRoute(  GET("/users/datas")             ,   handler::findAllUserDatas)
                .andRoute(  GET("/users/me")                ,   handler::getCurrentUser)
                .andRoute(  GET("/users/data/{username}")   ,   handler::getUserData)
                .andRoute(  GET("/users/{username}")         ,   handler::findUser);

    }




}
