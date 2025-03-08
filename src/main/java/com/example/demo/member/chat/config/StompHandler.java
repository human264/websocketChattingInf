package com.example.demo.member.chat.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    @Value(("${jwt.secretKey}"))
    private String secretKey;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("connect요청시 토큰 유효성 검증");
            //Token 추출
            String token = accessor.getFirstNativeHeader("Authorization");
            String bearerToken = token.substring(7);
            //토큰 검증
//            Claims claims =
                    Jwts.parserBuilder().setSigningKey(secretKey)
                            .build()
                            .parseClaimsJws(bearerToken).getBody();
            System.out.println("토큰 유효성 검증 완료");
        }
        return message;
    }
}
