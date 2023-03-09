package com.example.chatservicewebsocket.repository;

import com.example.chatservicewebsocket.model.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    Mono<ChatRoom> findBySenderIdAndReceiverId(String senderId, String receiverId);
}
