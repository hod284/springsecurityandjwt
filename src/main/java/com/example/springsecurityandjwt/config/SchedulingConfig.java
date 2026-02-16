package com.example.springsecurityandjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄링 기능 활성화
 * 
 * MonitoringService의 @Scheduled(fixedRate = 2000) 어노테이션이 작동하려면
 * @EnableScheduling이 필요합니다.
 * 
 * 이것이 없으면 sendMetrics() 메서드가 자동으로 호출되지 않아
 * 실시간 모니터링 기능이 작동하지 않습니다.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
