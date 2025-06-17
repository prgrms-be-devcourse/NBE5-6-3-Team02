package com.grepp.smartwatcha.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

// Spring 비동기 작업 처리를 위한 설정 클래스
// - @Async 어노테이션이 적용된 메서드를 별도의 스레드에서 비동기로 실행 가능하게 함
// - ThreadPoolTaskExecutor를 통해 스레드풀 커스터마이징
@Configuration
@EnableAsync
public class AsyncConfig {

  // 비동기 작업을 처리할 Executor Bean 정의
  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8); // 최소 스레드 수 (초기 생성 및 항상 유지)
    executor.setMaxPoolSize(16); // 최대 스레드 수 (큐가 가득 찼을 때 확장 가능한 최대 스레드 수)
    executor.setQueueCapacity(100); // 작업 큐 용량 (corePoolSize 이상일 경우 대기열에 저장될 수 있는 작업 수)
    executor.setThreadNamePrefix("AsyncMovie-"); // 생성되는 스레드 이름 prefix 설정 (디버깅 및 로깅 시 유용)
    executor.initialize(); // 초기화

    return executor;

  }
}
