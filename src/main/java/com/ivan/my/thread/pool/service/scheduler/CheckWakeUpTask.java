package com.ivan.my.thread.pool.service.scheduler;

import com.ivan.my.thread.pool.service.CheckExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CheckWakeUpTask implements Runnable {

    private final CheckExecutorService checkExecutorService;

    @Override
    public void run() {
        // Властью данной мне мною, повелеваю: проверки запуститесь!
        log.info("task executing...");
        checkExecutorService.checkWakeUp();
    }

}
