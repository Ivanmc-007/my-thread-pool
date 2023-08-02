package com.ivan.my.thread.pool.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * *NOTE:<p>
 * Аннотация @EnableAsync включает следующее поведение Spring:
 * <p>- Spring распознает методы, помеченные аннотацией @Async
 * <p>- Spring запускает эти методы в фоновом пуле потоков (background thread pool)
 * <p>- По умолчанию Spring использует SimpleAsyncTaskExecutor для асинхронного запуска методов.
 *
 * <p>Total Number Worker Threads = (CPUs in Your System) + 4
 * <p>For linux can be so:
 * <p>in linux console execute command /some-dir/other-dir> lscpu
 * <p>output of command:
 *   ...
 *   CPU(s): 4
 *   ...
 * <p>totNumWorkThread = 4+4 = 8
 * <p>Calculating maxPoolSize:
 * @see https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html
 * изначально можно установить временные
 */
@Log4j2
@Configuration
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

    @Bean
    @ConditionalOnMissingBean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        return (r, executor) -> log.error("check failed — {}", r);
    }

    @Bean
    public ThreadPoolExecutor generalThreadPoolExecutor(RejectedExecutionHandler rejectedExecutionHandler) {
        // Количество основных потоков, которые будут созданы и сохранены в пуле. При поступлении новой задачи,
        // если все основные потоки заняты и внутренняя очередь заполнена, пул может увеличиться до maxPoolSize
        int corePoolSize = 10;

        // максимальное количество потоков, разрешенных в пуле
        int maxPoolSize = 200;

        // это интервал времени, в течение которого избыточные потоки (экземпляры которых превышают corePoolSize)
        // могут существовать в состоянии простоя
        long keepAliveTime = 0;

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100),
                new BasicThreadFactory.Builder()
                        .namingPattern("general-pool-%d")
                        .daemon(true)
                        .priority(Thread.MAX_PRIORITY)
                        .build());
        threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);

        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor(ThreadPoolExecutor generalThreadPoolExecutor) {
        // ConcurrentTaskExecutor - class-adapter for converting ThreadPoolExecutor to TaskExecutor
        return new ConcurrentTaskExecutor(generalThreadPoolExecutor);
    }

}
