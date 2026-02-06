package com.example.springsecurityandjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
@Configuration
// 고수준에 websocket 메시지 브로커를 활성화
//Spring WebSocket + STOMP 메시지 브로커를 활성화하는 어노테이션
@EnableWebSocketMessageBroker
public class WebsoketConfig {
    
}
