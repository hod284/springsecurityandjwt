package com.example.springsecurityandjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
private String host;

@Value("${spring.data.redis.port}")
private int port;

@Bean
//“Redis 연결을 만들어주는 공장”
public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
    // RedisConnectionFactory 설정 코드 작성
    // 예: LettuceConnectionFactory 사용
    return new LettuceConnectionFactory(config);
   }
   @Bean
   public RedisTemplate<String, Object> redisTemplate() {
       RedisTemplate<String, Object> template = new RedisTemplate<>();
       template.setConnectionFactory(redisConnectionFactory());
       template.setKeySerializer(RedisSerializer.string());
       template.setHashKeySerializer(RedisSerializer.string());
       template.setValueSerializer(RedisSerializer.json());
       template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
       return template;
   }
}
