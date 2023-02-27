package com.ivan.my.thread.pool.service;

import com.ivan.my.thread.pool.model.dto.response.RespDto;
import com.ivan.my.thread.pool.model.entity.VlCheckQueue;
import com.ivan.my.thread.pool.repository.VlCheckQueueRepo;
import com.ivan.my.thread.pool.util.constant.RespStatus;
import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class WebClientRequestService {

    final List<Object> checks = new ArrayList<Object>() {
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("routeId", 5211);
            jsonObject.put("stageId", 4121);
            jsonObject.put("checkId", 233521);
            jsonObject.put("bidId", 1270756);
            jsonObject.put("checkCode", "BARS_1");
            this.add(jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("routeId", 5211);
            jsonObject2.put("stageId", 4121);
            jsonObject2.put("checkId", 233522);
            jsonObject2.put("bidId", 1270756);
            jsonObject2.put("checkCode", "BARS_2");
            this.add(jsonObject);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("routeId", 5211);
            jsonObject3.put("stageId", 4121);
            jsonObject3.put("checkId", 233523);
            jsonObject3.put("bidId", 1270756);
            jsonObject3.put("checkCode", "BARS_3");
            this.add(jsonObject);
        }
    };


    private final VlCheckQueueRepo vlCheckQueueRepo;

    @Qualifier(value = WebClientName.LOCAL_WEB_CLIENT)
    private final WebClient localWebClient;

    public <T> void action(Class<T> clazz) {
        int index = (int) (Math.random() * checks.size());
        Object check = checks.get(index);

        this.getRequest(clazz).timeout(Duration.ofSeconds(5L))
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
//                        vlCheckQueueRepo.save(entity);
                    } else if(RespStatus.ERROR.equalsIgnoreCase(respDto.getStatus())) {
                        System.out.println(respDto.getStatus());
                        System.out.println(respDto.getMessage());
                    } else if(RespStatus.OK.equalsIgnoreCase(respDto.getStatus())) {
                        this.extractAndSave(respDto);
                    } else {
                        System.out.println("Unknown status");
                    }

                });
    }

    protected <T> Mono<T> getRequest(Class<T> clazz, Object... args) {
        return localWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pool/sleep")
                        .build())
                .retrieve()
                .bodyToMono(clazz);
    }

    protected <T> void extractAndSave(RespDto<T> respDto) {

    }

    protected <T> void saveToQueue(T obj) {

    }

}
