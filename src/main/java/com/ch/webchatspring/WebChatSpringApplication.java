package com.ch.webchatspring;

import com.ch.webchatspring.service.heartbeat.HeartbeatServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.ch.webchatspring.mapper")
public class WebChatSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebChatSpringApplication.class, args);
    }

}
