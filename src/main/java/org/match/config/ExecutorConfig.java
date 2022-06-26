package org.match.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfig {

    @Bean("matchExecutor")
    public ThreadPoolTaskExecutor matchExecutor(){
        ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();

        poolExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        poolExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        poolExecutor.setQueueCapacity(100);
        poolExecutor.setKeepAliveSeconds(300);
        poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        poolExecutor.setThreadNamePrefix("match-pool");

        return poolExecutor;
    }
}
