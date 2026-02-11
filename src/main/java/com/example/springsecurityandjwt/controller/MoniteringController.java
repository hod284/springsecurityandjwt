package com.example.springsecurityandjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;





@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MoniteringController {
     private final MeterRegistry meterRegistry;
     
     @GetMapping("/meterics/cpu")
     public ResponseEntity<Map<String, Object>> GetCpuMetrics() {
        Map <String, Object> cpuMetrics = new HashMap<>();
        try 
        {      
            double systemCpuLoad = meterRegistry.get("system.cpu.usage").gauge().value();            
            double processCpuLoad = meterRegistry.get("process.cpu.usage").gauge().value();
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            cpuMetrics.put("systemCpuUsage", systemCpuLoad * 100);
            cpuMetrics.put("processCpuUsage", processCpuLoad * 100);
            cpuMetrics.put("cores", osBean.getAvailableProcessors());
            cpuMetrics.put("timestamp", System.currentTimeMillis());

        }
        catch (Exception e) {
            log.error("Error retrieving CPU metrics", e);
            return ResponseEntity.internalServerError().build();
        } 
        return ResponseEntity.ok(cpuMetrics);
     } 
     @GetMapping("/metrics/memory")
     public ResponseEntity<Map<String,Object>> getMemoryMetrics() {
         Map<String, Object> memoryMetrics = new HashMap<>();
            try 
            {
                double usedMemory = meterRegistry.get("jvm.memory.used").gauge().value();
                double maxMemory = meterRegistry.get("jvm.memory.max").gauge().value();
                double committedMemory = meterRegistry.get("jvm.memory.committed").gauge().value();
            
                 memoryMetrics.put("used", usedMemory / 1024 / 1024);
                 memoryMetrics.put("max", maxMemory / 1024 / 1024);
                 memoryMetrics.put("committed", committedMemory / 1024 / 1024);
                 memoryMetrics.put("percentage", (usedMemory / maxMemory) * 100);
                 memoryMetrics.put("timestamp", System.currentTimeMillis());
                    
            }
            catch (Exception e) {
                log.error("Error retrieving Memory metrics", e);
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok(memoryMetrics);
     }
     @GetMapping("/metrics/threads")
     public ResponseEntity<Map<String, Object>> getThreadMetrics(@RequestParam String param) {
            Map<String, Object> threadMetrics = new HashMap<>();
            try 
            {
                double threadCount = meterRegistry.get("jvm.threads.live").gauge().value();
                double peakThreads = meterRegistry.get("jvm.threads.peak").gauge().value();
                double daemonThreadCount = meterRegistry.get("jvm.threads.daemon").gauge().value();
                
                threadMetrics.put("liveThreads", threadCount);
                threadMetrics.put("peakThreads", peakThreads);
                threadMetrics.put("daemonThreads", daemonThreadCount);
                threadMetrics.put("timestamp", System.currentTimeMillis());
            }
            catch (Exception e) {
                log.error("Error retrieving Thread metrics", e);
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok(threadMetrics); 
     }
     @GetMapping("/metrics/all")
     public ResponseEntity<Map<String, Object>> getMethodName(@RequestParam String param) {
        Map<String, Object> allMetrics = new HashMap<>();
        
        try {
            Map<String, Object> cpu = new HashMap<>();
            cpu.put("systemCpuUsage", meterRegistry.get("system.cpu.usage").gauge().value() * 100);
            cpu.put("processCpuUsage", meterRegistry.get("process.cpu.usage").gauge().value() * 100);
            allMetrics.put("cpu", cpu);
            
            Map<String, Object> memory = new HashMap<>();
            double usedMemory = meterRegistry.get("jvm.memory.used").gauge().value();
            double maxMemory = meterRegistry.get("jvm.memory.max").gauge().value();
            memory.put("used", usedMemory / 1024 / 1024);
            memory.put("max", maxMemory / 1024 / 1024);
            memory.put("percentage", (usedMemory / maxMemory) * 100);
            allMetrics.put("memory", memory);
            
            Map<String, Object> threads = new HashMap<>();
            threads.put("live", meterRegistry.get("jvm.threads.live").gauge().value());
            threads.put("peak", meterRegistry.get("jvm.threads.peak").gauge().value());
            allMetrics.put("threads", threads);
            
            allMetrics.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
             log.error("Error retrieving all metrics", e);
            return ResponseEntity.internalServerError().build();
        }
        
        return ResponseEntity.ok(allMetrics);
     }
     
     
}
