package com.ivan.my.thread.pool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.my.thread.pool.model.dto.bars.CheckBarsResp;
import com.ivan.my.thread.pool.model.dto.bars.CheckBarsRespStatus;
import com.ivan.my.thread.pool.model.dto.response.RespDto;
import com.ivan.my.thread.pool.model.entity.VlCheckQueue;
import com.ivan.my.thread.pool.repository.VlCheckQueueRepo;
import com.ivan.my.thread.pool.util.constant.RespStatus;
import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CheckExecutorService {

    @Qualifier(WebClientName.LOCAL_WEB_CLIENT)
    private final WebClient localWebClient;

    private final ObjectMapper objectMapper;

    private final VlCheckQueueRepo vlCheckQueueRepo;

    public void checkWakeUp() {
        vlCheckQueueRepo.findWithInterval().forEach(this::checkExecute);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void checkExecute(@NonNull VlCheckQueue vlCheckQueue) {
        Mono<RespDto<String>> monoResp = this.getCheckResult(new ParameterizedTypeReference<RespDto<String>>() {
        });

        monoResp.onErrorResume(throwable -> Mono.just(RespDto.<String>builder()
                .status(RespStatus.ERROR)
                .message(throwable.getMessage())
                .build())).subscribe(respDto -> {
            if (RespStatus.ERROR.equalsIgnoreCase(respDto.getStatus())) {
                log.error(Thread.currentThread().getName() + " " + respDto.getStatus());
                log.error(respDto.getMessage(), new RuntimeException(respDto.getMessage()));
            } else if (RespStatus.OK.equalsIgnoreCase(respDto.getStatus())) {
                try {
                    CheckBarsResp checkBarsResp = this.objectMapper.readValue(respDto.getContent(), CheckBarsResp.class);
                    if (CheckBarsRespStatus.NOT_ANSWER.equalsIgnoreCase(checkBarsResp.getStatus())) {
                        // вытащить старые элементы из очереди (БД)
                        List<VlCheckQueue> oldElems = vlCheckQueueRepo.findWithFilter(null,
                                vlCheckQueue.getRouteId(), vlCheckQueue.getStageId(),
                                vlCheckQueue.getCheckId(), vlCheckQueue.getBidId(),
                                vlCheckQueue.getCheckCode());
                        // если таких нет, то поместить в очередь
                        if (oldElems.isEmpty()) {
                            vlCheckQueue.setCheckStart(LocalDateTime.now());
                            vlCheckQueue.setRequestCount(1);
                            vlCheckQueueRepo.save(vlCheckQueue);
                        } else {
                            // пройтись по элементам и увеличить номер запроса на 1
                            oldElems.forEach(vlCheckQueueOld -> {
                                Integer requestCount = vlCheckQueueOld.getRequestCount();
                                // если мы ходим достаточно много раз, то удалить элемент из очереди и
                                // записать ответ как отрицательный
                                if (requestCount + 1 >= 4) {
                                    vlCheckQueueRepo.deleteById(vlCheckQueueOld.getId());
                                    // TODO: записать ответ в БД
                                    // TODO: проверить по route_id и stage_id есть ли такие в 'очереди'
                                    // TODO: если таких нет, то завершить этап
                                } else {
                                    vlCheckQueueOld.setRequestCount(requestCount + 1);
                                    vlCheckQueueRepo.save(vlCheckQueueOld);
                                }
                            });
                        }
                        System.out.println("Нет ответа");
                    } else if (CheckBarsRespStatus.COMPLETE.equalsIgnoreCase(checkBarsResp.getStatus())) {
                        // если ответ получен, то 'подчистить' за собой
                        List<VlCheckQueue> oldElems = vlCheckQueueRepo.findWithFilter(null,
                                vlCheckQueue.getRouteId(), vlCheckQueue.getStageId(),
                                vlCheckQueue.getCheckId(), vlCheckQueue.getBidId(),
                                vlCheckQueue.getCheckCode());
                        for (VlCheckQueue oldElement : oldElems) {
                            vlCheckQueueRepo.deleteById(oldElement.getId());
                        }
                        // TODO: записать ответ в БД
                        // TODO: проверить по route_id и stage_id есть ли такие в 'очереди'
                        // TODO: если таких нет, то завершить этап
                        System.out.println("Есть ответ");
                    } else {
                        log.error("Статус от БАРСА неизвестен");
                    }
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } else {
                log.error("Статус ответа неизвестен!");
            }
        });

    }

    private <T> Mono<T> getCheckResult(ParameterizedTypeReference<T> parameterizedTypeReference) {
        return localWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/external/some-check")
                        .queryParam("id", (int) (Math.random() * 10))
                        .build())
                .retrieve()
                .bodyToMono(parameterizedTypeReference);
    }

    public <T> Mono<T> getCheckResult(Class<T> clazz) {
        return localWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/external/some-check")
                        .queryParam("id", (int) (Math.random() * 10))
                        .build())
                .retrieve()
                .bodyToMono(clazz);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void addChecks(List<VlCheckQueue> queueElements) {
        for (VlCheckQueue element : queueElements) {
            // изменить на 'if check exists'
            List<VlCheckQueue> oldElements = vlCheckQueueRepo.findWithFilter(null,
                    element.getRouteId(), element.getStageId(),
                    element.getCheckId(), element.getBidId(),
                    element.getCheckCode());
            // если такой проверки ещё нет в очереди, то добавить
            if(oldElements.isEmpty()) {
                element.setRequestCount(1);
                element.setCheckStart(LocalDateTime.now());
                vlCheckQueueRepo.save(element);
            }
        }
    }

}
