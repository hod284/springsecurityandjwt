package com.example.springsecurityandjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
@Configuration
// 고수준에 websocket 메시지 브로커를 활성화
//Spring WebSocket + STOMP 메시지 브로커를 활성화하는 어노테이션
@EnableWebSocketMessageBroker
public class WebsoketConfig implements WebSocketMessageBrokerConfigurer {
    
 @Override
 public void configureMessageBroker(MessageBrokerRegistry config) {
    //메시지 브로커 구성
    config.enableSimpleBroker("/topic", "/queue"); //메시지 브로커의 접두사 설정
    config.setApplicationDestinationPrefixes("/app"); //애플리케이션 목적지 접두사 설정
 }
  @Override 
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    //STOMP 엔드포인트 등록
    registry.addEndpoint("/ws-monitoring").setAllowedOriginPatterns("*").withSockJS(); //클라이언트가 연결할 수 있는 엔드포인트 설정
  }

}
