package com.example.chatservicewebsocket.repository;

import com.example.chatservicewebsocket.model.ChatMessage;
import com.example.chatservicewebsocket.model.MessageStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Mono<Long> countBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, MessageStatus status);
    Flux<ChatMessage> findByChatId(String chatId);
}
