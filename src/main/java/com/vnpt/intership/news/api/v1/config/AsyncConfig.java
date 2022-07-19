package com.vnpt.intership.news.api.v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(25);
        executor.setKeepAliveSeconds(200);
        executor.setThreadNamePrefix("asyncThread");
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//        executor.setAwaitTerminationSeconds(60);

//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    // create Executor cho Async
    @Bean("applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}
