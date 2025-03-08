package com.example.demo.member.chat.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
//    private final SimpleWebSocketHandler simpleWebSocketHandler;
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        //connect url로 websocket을 연결할 수 있도록 설정
//        registry.addHandler(simpleWebSocketHandler, "/connect")
//                .setAllowedOrigins("http://localhost:3000");
//    }
//}
