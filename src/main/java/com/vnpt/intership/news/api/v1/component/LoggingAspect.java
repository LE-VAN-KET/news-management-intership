package com.vnpt.intership.news.api.v1.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    private long startTime;

    @Before("execution(* com.vnpt.intership.news.api.v1.controller.*.*(..))")
    public void logBeforeController(@NotNull JoinPoint joinPoint) {
        this.startTime = System.nanoTime();
//        log.info("API: {}.{}", joinPoint.getSignature().getDeclaringType().getSimpleName(),
//                joinPoint.getSignature().getName());
    }

    @After("execution(* com.vnpt.intership.news.api.v1.controller.*.*(..))")
    public void logAfterController(@NotNull JoinPoint joinPoint) {
        long endTime = System.nanoTime();
        long timeTaken = TimeUnit.MILLISECONDS.convert(endTime - this.startTime, TimeUnit.NANOSECONDS);
        log.info("API: {}.{}, time={}, unit={}", joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), timeTaken, TimeUnit.MILLISECONDS);
    }
}
