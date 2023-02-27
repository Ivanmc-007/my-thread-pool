package com.ivan.my.thread.pool.controller;

import com.ivan.my.thread.pool.model.dto.response.RespDto;
import com.ivan.my.thread.pool.model.entity.VlCheckQueue;
import com.ivan.my.thread.pool.service.CheckExecutorService;
import com.ivan.my.thread.pool.service.check.Check;
import com.ivan.my.thread.pool.service.check.CheckBars16Component;
import com.ivan.my.thread.pool.service.check.CheckBars17Component;
import com.ivan.my.thread.pool.service.check.CheckBarsData;
import com.ivan.my.thread.pool.service.delay.DelayedScheduler;
import com.ivan.my.thread.pool.util.constant.RespStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
//@Log4j2
@RestController
@RequiredArgsConstructor
public class CheckExecutorController {

    private final CheckExecutorService checkExecutorService;

    private final CheckBars16Component checkBars16Component;
    private final CheckBars17Component checkBars17Component;

    private final DelayedScheduler delayedScheduler;


    public RespDto<String> action() {
        System.out.println("action..." + Thread.currentThread().getName());
        VlCheckQueue vlCheckQueue = VlCheckQueue.builder()
                .routeId(5142L)
                .stageId(4041L)
                .checkId(1201L)
                .bidId(126776L)
                .checkCode("BARS_16")
                .build();
        checkExecutorService.checkExecute(vlCheckQueue);
        return RespDto.<String>builder()
                .status(RespStatus.OK)
                .message(RespStatus.OK)
                .content("Проверки запущены")
                .build();
    }

    @Async("generalThreadPoolExecutor")
    @GetMapping("/async")
    public /*CompletableFuture<RespDto<String>>*/void useCustomPool() throws InterruptedException {

        int numOfCores = Runtime.getRuntime().availableProcessors();
        System.out.println(Thread.currentThread().getName() + " started...");
        Thread.sleep(4000);
        System.out.println(Thread.currentThread().getName() + " end ... result: " + numOfCores);
        /*
        return checkExecutorService.getCheckResult(String.class)
                .flatMap(s -> {
                    return Mono.just(RespDto.<String>builder()
                            .message("ok")
                            .status("ok")
                            .content(s).build());
                })
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Mono.just(RespDto.<String>builder()
                            .status("error")
                            .message(throwable.getMessage())
                            .build());
                }).log()
                .doOnNext(stringRespDto -> {
                    System.out.println("*");
                    System.out.println(stringRespDto);
                })
                .toFuture();*/
//        Mono<String> strResult = Mono.just("one-one-one");
//        return strResult.toFuture();
//        return CompletableFuture.completedFuture(strResult);
    }

    @GetMapping(value = "/check-executor/run", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespDto<String> initChecks() {
        List<VlCheckQueue> queueElements = new ArrayList<VlCheckQueue>() {
            {
                this.add(VlCheckQueue.builder()
                        .routeId(5142L)
                        .stageId(4041L)
                        .checkId(1201L)
                        .bidId(126776L)
                        .checkCode("BARS_16")
                        .build());
                this.add(VlCheckQueue.builder()
                        .routeId(3321L)
                        .stageId(4041L)
                        .checkId(1201L)
                        .bidId(126776L)
                        .checkCode("BARS_16")
                        .build());
                this.add(VlCheckQueue.builder()
                        .routeId(3361L)
                        .stageId(4041L)
                        .checkId(1201L)
                        .bidId(126776L)
                        .checkCode("BARS_16")
                        .build());
                this.add(VlCheckQueue.builder()
                        .routeId(5142L)
                        .stageId(4041L)
                        .checkId(232431L)
                        .bidId(1270756L)
                        .checkCode("BARS_17")
                        .build());
                this.add(VlCheckQueue.builder()
                        .routeId(5142L)
                        .stageId(4041L)
                        .checkId(832431L)
                        .bidId(1280756L)
                        .checkCode("BARS_17")
                        .build());
            }
        };
        checkExecutorService.addChecks(queueElements);
        return RespDto.<String>builder()
                .status(RespStatus.OK)
                .message(RespStatus.OK)
                .content("Проверки запущены")
                .build();
    }

    @GetMapping(value = "/check-executor/delay-task", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delayTask() {
        VlCheckQueue check = VlCheckQueue.builder()
                .routeId(5142L)
                .stageId(4041L)
                .checkId(1201L)
                .bidId(126776L)
                .checkCode("BARS_16")
                .build();
        delayedScheduler.schedule(() -> {
            int count = (int) (Math.random() * 5 + 1);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                stringBuilder.append("*");
            }
            for (int i = 0; i < 4; i++) {
                try {
                    System.out.println(stringBuilder.toString() + check);
                    Thread.sleep(15000L);
                    if (i == count)
                        break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0L);
    }

    @GetMapping(value = "/run-task-bars16", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespDto<String> runTaskBars16() throws InterruptedException {
        List<Check<?, ?>> checks = new ArrayList<>();
        checks.add(checkBars16Component);
        checks.add(checkBars17Component);
        checks.forEach(Check::checkExecute);


//        checkBars16Component.checkExecute(new CheckBarsData()).subscribe(checkBars16Resp -> {
//            System.out.println(checkBars16Resp);
//        });


//        delayedScheduler.schedule(() -> {
//            for (int i = 0; i < 4; i++) {
//                checkBars16Component.checkExecute(new CheckBarsData()).subscribe(checkBars16Resp -> {
//                    if (CheckBarsRespStatus.NOT_ANSWER.equalsIgnoreCase(checkBars16Resp.getStatus())) {
//                        System.out.println(checkBars16Resp);
//                    } else {
//                        System.out.println(checkBars16Resp);
//                    }
//                });
//            }
//        }, 20000);

//        new DelayTask(() -> {
//            for (int i = 0; i < 4; i++) {
//                checkBars16Component.checkExecute(new CheckBarsData()).subscribe(checkBars16Resp -> {
//                    if (CheckBarsRespStatus.NOT_ANSWER.equalsIgnoreCase(checkBars16Resp.getStatus())) {
//                        try {
//                            System.out.println(checkBars16Resp);
//                            Thread.sleep(3000L);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        System.out.println(checkBars16Resp);
//                    }
//                });
//            }
//
//        }, 0);


        return RespDto.<String>builder()
                .status(RespStatus.OK)
                .message(RespStatus.OK)
                .content("Проверки запущены")
                .build();
    }

    @GetMapping(value = "/run-task-bars17", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespDto<String> runTaskBars17() {

        checkBars17Component.checkExecute(new CheckBarsData()).subscribe(checkBars17Resp -> {
            List<Check> l = new ArrayList<>();
            if (checkBars17Resp instanceof Check) {
//                l.add
                l.add((Check) checkBars17Resp);
            }
            System.out.println(checkBars17Resp);

        });

        return RespDto.<String>builder()
                .status(RespStatus.OK)
                .message(RespStatus.OK)
                .content("Проверки запущены")
                .build();
    }


}
