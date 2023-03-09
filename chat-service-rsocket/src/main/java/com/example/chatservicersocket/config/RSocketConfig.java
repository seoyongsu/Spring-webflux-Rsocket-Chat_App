package com.example.chatservicersocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class RSocketConfig {
//    @Bean
//    public RSocketServerFactoryCustomizer rSocketServerFactoryCustomizer() {
//        return factory -> factory
//                .route("example.request-response")
//                .addHandler(new ExampleRequestResponseHandler())
//                .route("example.fire-and-forget")
//                .addHandler(new ExampleFireAndForgetHandler())
//                .route("example.request-stream")
//                .addHandler(new ExampleRequestStreamHandler())
//                .route("example.request-channel")
//                .addHandler(new ExampleRequestChannelHandler());
//    }
//
//    @Bean
//    public RSocketStrategies rSocketStrategies() {
//        return RSocketStrategies.builder()
//                .encoder(new Jackson2JsonEncoder())
//                .decoder(new Jackson2JsonDecoder())
//                .build();
//    }
//
//    @Bean
//    public RSocketMessageHandler rSocketMessageHandler(RSocketStrategies rSocketStrategies) {
//        RSocketMessageHandler handler = new RSocketMessageHandler();
//        handler.setRSocketStrategies(rSocketStrategies);
//        return handler;
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> rSocketRouter(RSocketMessageHandler handler) {
//        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
//        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        mapping.registerMapping(RequestMappingInfo
//                .paths("/**")
//                .methods(RequestMethod.values())
//                .produces(MediaType.APPLICATION_JSON_VALUE), handler);
//
//        RouterFunction<ServerResponse> routerFunction = RouterFunctions
//                .toWebHandler(WebHandler.fromHandlerMapping(mapping), new ResponseStatusExceptionHandler());
//        return routerFunction;
//    }
}
