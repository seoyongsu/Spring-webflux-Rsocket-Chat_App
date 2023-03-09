package com.example.chatservicersocket.service;

import com.example.chatservicersocket.model.ChatMessage;
import com.example.chatservicersocket.model.ChatNotification;
import com.example.chatservicersocket.model.MessageStatus;
import com.example.chatservicersocket.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatRoomService chatRoomService;
    
    private final ChatMessageRepository messageRepository;

    /**
     * 메세지 데이터 저장
     */
    public Mono<ChatMessage> save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        return messageRepository.save(chatMessage);
    }

    /**
     * 메세지 리스트 불러오기
     * @param senderId
     * @param receiverId
     * @return
     */
    public Flux<ChatMessage> findChatMessages(String senderId, String receiverId){
        Mono<String> chatId = chatRoomService.getChatId(senderId,receiverId, false);
        Flux<ChatMessage> message = chatId.flatMapMany(cId-> messageRepository.findByChatId(cId));
        return message
                .doOnNext(next ->  {
                    //상태 읽음으로 변경
                    if(next.getSenderId().equals(receiverId) && next.getReceiverId().equals(senderId) && next.getStatus().equals(MessageStatus.RECEIVED)){
                        next.setStatus(MessageStatus.DELIVERED);
                        messageRepository.save(next).subscribe();
                    }
                });
    }



    /**
     * 새로운 메세지 갯수 확인
     */
    public Mono<Long> countNewMessages(String senderId, String receiverId) {
        return messageRepository.countBySenderIdAndReceiverIdAndStatus(senderId, receiverId, MessageStatus.RECEIVED);
    }


    public Mono<ChatMessage> findById(String id) {
        return messageRepository.findById(id).flatMap(chatMessage -> {
            chatMessage.setStatus(MessageStatus.DELIVERED);
            return messageRepository.save(chatMessage);
        });
    }


}
