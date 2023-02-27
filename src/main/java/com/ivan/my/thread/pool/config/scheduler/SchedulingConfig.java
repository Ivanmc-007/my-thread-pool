package com.ivan.my.thread.pool.config.scheduler;

import com.ivan.my.thread.pool.service.scheduler.CheckWakeUpTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private CheckWakeUpTask checkWakeUpTask;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        // создать задачу запускаемую по cron расписанию
        CronTask cronTask = new CronTask(checkWakeUpTask, "0 */10 * ? * *");
        // зарегистрировать задачу
        taskRegistrar.setCronTasksList(Collections.singletonList(cronTask));
    }

}
