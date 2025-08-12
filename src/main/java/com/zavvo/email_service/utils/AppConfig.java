package com.zavvo.email_service.utils;


import com.zavvo.common_utils.shared.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
