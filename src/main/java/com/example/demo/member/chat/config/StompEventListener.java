package com.example.demo.member.chat.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 스프링과 stomp는 기본적으로 세션관리를 자동(내부적)으로 처리
// 연결/해제 이벤트를 기록, 연결 된 세션수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성 => 로그, 디버깅 목적
@Component

public class StompEventListener {
    private final Set<String> set = ConcurrentHashMap.newKeySet();

    @EventListener
    public void connectHandle(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        set.add(accessor.getSessionId());
        System.out.println("connect session ID : " + accessor.getSessionId());
        System.out.println("total session : " + set.size());
    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        set.remove(accessor.getSessionId());
        System.out.println("disconnect session ID : " + accessor.getSessionId());
        System.out.println("total session : " + set.size());
    }

}
