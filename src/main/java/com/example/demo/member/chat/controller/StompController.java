package com.example.demo.member.chat.controller;

import com.example.demo.member.chat.dto.ChatMessageDto;
import com.example.demo.member.chat.service.ChatService;
import com.example.demo.member.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {
//
//    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    // 방법1. MessageMapping(수신)과 SendTo(topic에 메세지 전달) 한꺼 번에 처리
//    @MessageMapping("/{roomId}")
//    @SendTo("/topic/{roomId}") //해당 roomId에 메세지를 발행하여 구독중인 클라이언트에게 메세지 전송
//    // DestinationVariable을 @MessageMapping 어노테이션으로 정의된 WebSocket Controller 내에서만 사용
//    public String sendMessage(@DestinationVariable Long roomId, String message) {
//        System.out.println("roomId : " + roomId + ", message : " + message);
//        return message;
//    }
    private final RedisPubSubService redisPubSubService;

    //방법2. MessageMapping어노테이션만 활용.
    @MessageMapping("/{roomId}")
    private void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageReqDto) throws JsonProcessingException {
        System.out.println("roomId : " + roomId + ", message : " + chatMessageReqDto.getMessage());
        chatService.saveMessage(roomId, chatMessageReqDto);
        chatMessageReqDto.setRoomId(roomId);
//        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageReqDto);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageReqDto);
        redisPubSubService.publish("chat", message);
    }

}
