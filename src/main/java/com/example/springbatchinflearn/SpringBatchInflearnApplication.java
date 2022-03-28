package com.example.springbatchinflearn;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing // 스프링 배치 수행을 위한 모든 빈 올림
public class SpringBatchInflearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchInflearnApplication.class, args);
    }

}
