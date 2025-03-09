package com.example.demo.member.chat.config;


import com.example.demo.member.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {


    @Value(("${jwt.secretKey}"))
    private String secretKey;

    @Autowired
    private ChatService chatService;

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
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            System.out.println("connect요청시 토큰 유효성 검증");
            //Token 추출
            String token = accessor.getFirstNativeHeader("Authorization");
            String bearerToken = token.substring(7);
            //토큰 검증
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(bearerToken).getBody();
            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];

            if (!chatService.isRoomParticipant(email, Long.parseLong(roomId))) {
                throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
            }
            System.out.println("토큰 유효성 검증 완료");
        }


        return message;
    }
}
