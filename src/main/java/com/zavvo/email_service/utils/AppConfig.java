package com.zavvo.email_service.utils;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zavvo.shared.ExponentialBackoffRetry;

@Configuration
public class AppConfig {

    @Bean
    public ExponentialBackoffRetry exponentialBackoffRetry(){
        return new ExponentialBackoffRetry(
                5,
                1500,
                100000
        );
    }
}
