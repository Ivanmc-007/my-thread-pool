package com.ivan.my.thread.pool.service;

import com.ivan.my.thread.pool.model.dto.response.RespDto;
import com.ivan.my.thread.pool.model.entity.VlCheckQueue;
import com.ivan.my.thread.pool.util.constant.RespStatus;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public abstract class CheckService {

    protected abstract <T> Mono<T> getRequest(Class<T> clazz, Object ... args);

    protected abstract <T> void extractAndSave(RespDto<T> respDto);

    protected abstract <T> void saveToQueue(T obj);

    public void initiateCheck(Object check) {
        this.getRequest(String.class).timeout(Duration.ofSeconds(5L))
                .map(s -> RespDto.builder()
                        .status(RespStatus.OK)
                        .message(RespStatus.OK)
                        .content(s)
                        .build())
                .onErrorResume(e -> {
                    if (e instanceof TimeoutException) {
                        System.out.println(e.getMessage());
                        return Mono.just(RespDto.builder()
                                .status(RespStatus.WARNING)
                                .message(e.getMessage())
                                .build());
                    } else {
                        return Mono.just(RespDto.builder()
                                .status(RespStatus.ERROR)
                                .message(e.getMessage())
                                .build());
                    }
                }).subscribe(respDto -> {
                    if(RespStatus.WARNING.equalsIgnoreCase(respDto.getStatus())) {
                        // добавить в очередь ... выполнять потом
//                        VlCheckQueue entity = VlCheckQueue.builder()
//                                .data(check.toString())
//                                .status("NOT_READY")
//                                .dateExecute(LocalDateTime.now().plusMinutes(2))
//                                .build();
//                        // сохраняем в очередь если WARNING
//                        this.saveToQueue(entity);
                    } else if(RespStatus.ERROR.equalsIgnoreCase(respDto.getStatus())) {
                        System.out.println(respDto.getStatus());
                        System.out.println(respDto.getMessage());
                    } else if(RespStatus.OK.equalsIgnoreCase(respDto.getStatus())) {
                        // сохраняем хороший результат в БД если ОК
                        this.extractAndSave(respDto);
                    } else {
                        System.out.println("Unknown status");
                    }

                });
    }

}
