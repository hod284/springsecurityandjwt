package com.example.springsecurityandjwt.Servise;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
@RequiredArgsConstructor
// 모나토랑서비스
public class MonitoringService {
    //SimpMessagingTemplate 는 Spring WebSocket(STOMP) 에서서버가 클라이언트에게 직접 메시지를 보낼 때 사용하는 객체야.
  private final SimpMessagingTemplate messagingTemplate;
  /*
  Micrometer에서 메트릭(지표)을 등록하고 관리하는 중심 객체
  CPU 사용량
메모리 사용량
HTTP 요청 수
응답 시간
DB 커넥션 수
커스텀 카운터
  */
  private final MeterRegistry meterRegistry;
 /**
     * 2초마다 메트릭을 수집하여 WebSocket으로 전송
     */
   @Scheduled(fixedRate = 2000)
   private void sendMetrics() {
     try
     {
         Map<String,Object> metrics = collectmetrix();
         messagingTemplate.convertAndSend("/topic/metrics", (Object)metrics);
         log.debug("Metrics sent: CPU={}%, Memory={}%", 
                metrics.get("cpu") != null ? ((Map<?,?>)metrics.get("cpu")).get("system") : "N/A",
                metrics.get("memory") != null ? ((Map<?,?>)metrics.get("memory")).get("percentage") : "N/A"
            );
     }
     catch(Exception e)
     {
       log.error("Error collecting or sending metrics: {}", e.getMessage());
     }
   }
   private Map<String,Object> collectmetrix()
   {
     Map<String, Object> metrics = new HashMap<>();
      metrics.put("cpu", collectCpuMetrics());
      metrics.put("memory", collectMemoryMetrics());
      metrics.put("threads", collectThreadMetrics());
      metrics.put("system", collectSystemInfo());
      metrics.put("timestamp", System.currentTimeMillis());
        
      return metrics;
   }
    private Map<String,Object> collectCpuMetrics() 
    {
        Map<String,Object> cpu = new HashMap<>();
        try
        {
            Double systemCpu = meterRegistry.get("system.cpu.usage").gauge().value() * 100;
            Double processCpu = meterRegistry.get("process.cpu.usage").gauge().value() * 100;
            cpu.put("system", String.format("%.2f", systemCpu));
            cpu.put("process", String.format("%.2f", processCpu));
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            cpu.put("cores", osBean.getAvailableProcessors());

        }  
        catch (Exception e)
        {
        log.warn("Failed to collect CPU metrics", e);
            cpu.put("system", 0.0);
            cpu.put("process", 0.0);
        }
        return cpu;
    }
    private Map<String,Object> collectMemoryMetrics() 
    {
        Map<String,Object> memory = new HashMap<>();
        try
        {
          double usedMemory = meterRegistry.get("jvm.memory.used").gauge().value();
            double maxMemory = meterRegistry.get("jvm.memory.max").gauge().value();
            double committedMemory = meterRegistry.get("jvm.memory.committed").gauge().value();
            
            memory.put("used", usedMemory / 1024 / 1024);
            memory.put("max", maxMemory / 1024 / 1024);
            memory.put("committed", committedMemory / 1024 / 1024);
            memory.put("percentage", (usedMemory / maxMemory) * 100);
            
            double heapUsed = meterRegistry.get("jvm.memory.used")
                    .tag("area", "heap").gauge().value();
            double nonHeapUsed = meterRegistry.get("jvm.memory.used")
                    .tag("area", "nonheap").gauge().value();
            
            memory.put("heapUsed", heapUsed / 1024 / 1024);
            memory.put("nonHeapUsed", nonHeapUsed / 1024 / 1024);
        }
        catch (Exception e)
        {
            log.warn("Failed to collect Memory metrics", e);
            memory.put("used", 0.0);
            memory.put("max", 0.0);
            memory.put("percentage", 0.0);
        }
        return memory;
    }
    private Map<String,Object> collectThreadMetrics() 
    {
        Map<String,Object> threads = new HashMap<>();
        try
        {
            double threadCount = meterRegistry.get("jvm.threads.live").gauge().value();
            double daemonThreadCount = meterRegistry.get("jvm.threads.daemon").gauge().value();
            double peakThreadCount = meterRegistry.get("jvm.threads.peak").gauge().value();
            
            threads.put("live", threadCount);
            threads.put("daemon", daemonThreadCount);
            threads.put("peak", peakThreadCount);
        }
        catch (Exception e)
        {
            log.warn("Failed to collect Thread metrics", e);
            threads.put("live", 0);
            threads.put("daemon", 0);
            threads.put("peak", 0);
        }
        return threads;
    }
    private Map<String,Object> collectSystemInfo() 
    {
        Map<String,Object> system = new HashMap<>();
        try
        {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            system.put("osName", osBean.getName());
            system.put("osVersion", osBean.getVersion());
            system.put("architecture", osBean.getArch());
            system.put("availableProcessors", osBean.getAvailableProcessors());
            Runtime runtime = Runtime.getRuntime();
            system.put("jvmTotalMemory", runtime.totalMemory() / 1024 / 1024);
            system.put("jvmFreeMemory", runtime.freeMemory() / 1024 / 1024);
            system.put("jvmMaxMemory", runtime.maxMemory() / 1024 / 1024);
        }
        catch (Exception e)
        {
            log.warn("Failed to collect System Info", e);
            system.put("osName", "unknown");
            system.put("osVersion", "unknown");
            system.put("architecture", "unknown");
            system.put("availableProcessors", 0);
        }
        return system;
    }

}
