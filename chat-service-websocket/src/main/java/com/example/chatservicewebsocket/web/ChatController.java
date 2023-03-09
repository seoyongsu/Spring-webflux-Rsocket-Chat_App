package com.example.chatservicewebsocket.web;

import com.example.chatservicewebsocket.model.ChatMessage;
import com.example.chatservicewebsocket.model.ChatNotification;
import com.example.chatservicewebsocket.service.ChatMessageService;
import com.example.chatservicewebsocket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;


    @MessageMapping("/chat")
    public Mono<Void> processMessage(@Payload ChatMessage chatMessage) {
        log.info("MessageMapping  init ");

        Mono<String> chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getReceiverId(), true);
        return chatId.flatMap(id->{
            chatMessage.setChatId(id);
            return chatMessageService.save(chatMessage)
                    .doOnNext(saved->{
                        System.out.println("saved  :::  " + saved);

                        messagingTemplate.convertAndSendToUser(
                                chatMessage.getReceiverId(),
                                "/queue/messages",
                                new ChatNotification(saved.getId(), saved.getSenderId(), saved.getSenderName())
                        );
                    });
        }).then();
    }

    @GetMapping("/messages/{senderId}/{receiverId}/count")
    public Mono<Long> countNewMessages(@PathVariable String senderId, @PathVariable String receiverId){
        return chatMessageService.countNewMessages(senderId, receiverId);
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public Flux<ChatMessage> findChatMessages(@PathVariable String senderId, @PathVariable String receiverId){
        return chatMessageService.findChatMessages(senderId,receiverId);
    }

    @GetMapping("/messages/{id}")
    public Mono<ChatMessage> findMessage(@PathVariable String id){
        return chatMessageService.findById(id);
    }
}
