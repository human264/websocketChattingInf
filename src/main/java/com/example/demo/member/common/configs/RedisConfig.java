package com.example.demo.member.common.configs;

import com.example.demo.member.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;


    //연결 기본 객체
    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(Integer.parseInt(port));
        configuration.setDatabase(0);
        // redis pub/sub에서는 특정 데이터 베이스에 의존 적이지 않음
        // configuration.setDatabase(0);

        return new LettuceConnectionFactory(configuration);
    }

    //publish 객체
    @Bean
    @Qualifier("chatPubSub")
    //일반적으로 RedisTemplate<키, 데이터> 사용
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
    //subscribe 객체

    //publish 객체
    @Bean
    @Qualifier("chatPubSub")
    //일반적으로 RedisTemplate<키, 데이터> 사용
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub")
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter messageListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));

        return container;
    }

    //Redis에서 수신 된 메세지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }
}
