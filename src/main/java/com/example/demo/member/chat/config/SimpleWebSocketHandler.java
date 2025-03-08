package com.example.demo.member.chat.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        System.out.println("연결 성공 : " + session.getId());
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        System.out.println("메시지 수신 : " + payload);
//        for (WebSocketSession webSocketSession : sessions) {
//            if(webSocketSession.isOpen()) {
//                webSocketSession.sendMessage(new TextMessage(payload));
//            }
//
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        System.out.println("연결 종료 : " + session.getId());
//    }
//}
