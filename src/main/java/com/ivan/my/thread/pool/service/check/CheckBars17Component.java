package com.ivan.my.thread.pool.service.check;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.my.thread.pool.model.dto.bars.CheckBarsRespStatus;
import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Log4j2
@Component
@RequiredArgsConstructor
public class CheckBars17Component implements Check<CheckBarsData, Mono<CheckBars17Resp>> {

    @Qualifier(WebClientName.LOCAL_WEB_CLIENT)
    private final WebClient localWebClient;

    private final ObjectMapper objectMapper;

    public Mono<CheckBars17Resp> checkExecute(CheckBarsData params) {
        return this.getCheckResult(String.class, params).flatMap(respStr -> {
                    // обязательно записать в логи
                    log.info(respStr);
                    try {
                        if (respStr == null)
                            return Mono.empty();
                        CheckBars17Resp checkBars17Resp = objectMapper.readValue(respStr, CheckBars17Resp.class);
                        return Mono.just(checkBars17Resp);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return Mono.error(e);
                    }
                })
                // установи задержку между походами на external сервис
                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(5)))
                // сходи на external сервис, пока не выполнится условие
                .takeUntil(checkBars17Resp -> {
                    System.out.println("*");
                    return CheckBarsRespStatus.COMPLETE.equalsIgnoreCase(checkBars17Resp.getStatus());
                })
                // при этом, сходи не более 3-х раз (возьми первые 3 элемента)
                .take(3)
                .last();
    }

    private <T> Mono<T> getCheckResult(Class<T> clazz, Object... args) {
        return localWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/external/some-check/bars-17")
                        .queryParam("id", (int) (Math.random() * 10))
                        .build())
                .retrieve()
                .bodyToMono(clazz);
    }

    @Override
    public Mono<CheckBars17Resp> checkExecute() {
        // TODO: заглушка
        return this.checkExecute(new CheckBarsData());
    }

}
