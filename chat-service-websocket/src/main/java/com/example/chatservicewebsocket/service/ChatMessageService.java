package com.example.chatservicewebsocket.service;

import com.example.chatservicewebsocket.model.ChatMessage;
import com.example.chatservicewebsocket.model.MessageStatus;
import com.example.chatservicewebsocket.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatRoomService chatRoomService;
    
    private final ChatMessageRepository messageRepository;

    /**
     * 메세지 보내기
     * 메세지 데이터 저장
     */
    public Mono<ChatMessage> save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        return messageRepository.save(chatMessage);
    }

    /**
     * 새로운 메세지 갯수 확인
     */
    public Mono<Long> countNewMessages(String senderId, String receiverId) {
        return messageRepository.countBySenderIdAndReceiverIdAndStatus(senderId, receiverId, MessageStatus.RECEIVED);
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

        //상태값 변경 진행
        Flux<ChatMessage> update = message.filter(f-> f.getSenderId().equals(senderId) && f.getReceiverId().equals(receiverId));
        update.flatMap(up -> {
            up.setStatus(MessageStatus.DELIVERED);
            return messageRepository.save(up);
        }).subscribe();


        return message;
    }

    public Mono<ChatMessage> findById(String id) {
        return messageRepository.findById(id).flatMap(chatMessage -> {
            chatMessage.setStatus(MessageStatus.DELIVERED);
            return messageRepository.save(chatMessage);
        });
    }


    public void sinks(){

    }
}
