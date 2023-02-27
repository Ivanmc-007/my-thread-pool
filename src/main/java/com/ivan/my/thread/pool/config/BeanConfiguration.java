package com.ivan.my.thread.pool.config;

import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.Persistence;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
@Configuration
public class BeanConfiguration {

    // Total Number Worker Threads = (CPUs in Your System) + 4
    // For linux can be so:
    // in linux console execute command /some-dir/other-dir> lscpu
    // output of command:
    // ...
    // CPU(s): 4
    // ...
    // totNumWorkThread = 4+4 = 8
    @Bean
    public ThreadPoolExecutor myCustomPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,
        200,
        0,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(100),
        new BasicThreadFactory.Builder()
                .namingPattern("custom-pool-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build());
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.error("Some description about overflow pool : {}", r);
        });

        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }


    @Bean(value = WebClientName.LOCAL_WEB_CLIENT)
    public WebClient localWebClient() {
        return WebClient.create("http://127.0.0.1:9333/api/pool");
    }

}
