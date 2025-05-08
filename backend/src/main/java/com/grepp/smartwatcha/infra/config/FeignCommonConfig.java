package com.grepp.smartwatcha.infra.config;

import feign.Request.Options;
import feign.RequestInterceptor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableFeignClients("com.grepp.smartwatcha")
public class FeignCommonConfig {

    @Bean
    public Options requestOptions(){
        return new Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }
    
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            log.info("=========================================================");
            log.info("requestTemplate : {}", requestTemplate);
        };
    }

}
