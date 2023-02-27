package com.ivan.my.thread.pool.service.check;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.my.thread.pool.model.dto.bars.CheckBarsRespStatus;
import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

//import reactor.retry.Repeat;

@Log4j2
@Component
@RequiredArgsConstructor
public class CheckBars16Component implements Check<CheckBarsData, Mono<CheckBars16Resp>> {

    @Qualifier(WebClientName.LOCAL_WEB_CLIENT)
    private final WebClient localWebClient;

    private final ObjectMapper objectMapper;

    public Mono<CheckBars16Resp> checkExecute(CheckBarsData params) {
        return this.getCheckResult(String.class, params).flatMap(respStr -> {
            // обязательно записать в логи
            log.info(respStr);
            try {
                if (respStr == null)
                    return Mono.empty();
                CheckBars16Resp checkBars16Resp = objectMapper.readValue(respStr, CheckBars16Resp.class);
                return Mono.just(checkBars16Resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Mono.error(e);
            }
        })
                // установи задержку между походами на external сервис
                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(5)))
                // сходи на external сервис, пока не выполнится условие
                .takeUntil(checkBars16Resp -> {
                    System.out.println("*");
                    return CheckBarsRespStatus.COMPLETE.equalsIgnoreCase(checkBars16Resp.getStatus());
                })
                // при этом, сходи не более 3-х раз (возьми первые 3 элемента)
                .take(3)
                .last();
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

    protected <T> Mono<T> getCheckResult(Class<T> clazz, Object... args) {
        return localWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/external/some-check")
                        .queryParam("id", (int) (Math.random() * 10))
                        .build())
                .retrieve()
                .bodyToMono(clazz);

//                .repeatWhen(rere-> {return rere.next();})
//                .repeatWhen(completed -> completed.delayElements(Duration.ofSeconds(5))).next().flatMap(t -> {
//                    return Mono.just(t);
//                })
//                .repeat(5).skipUntil(t -> )
//                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(30)))

//                .takeUntil()
//                .last();
                /*.repeatWhen(Repeat.onlyIf(repeatContext -> {
                                    System.out.println(repeatContext);
                                    return true;
                                })
                                // фиксированная задержка отсрочки применяется перед каждым повтором
                                .fixedBackoff(Duration.ofSeconds(5))
                                .timeout(Duration.ofSeconds(11))*/
//                ).last();
//                .repeatWhen(Repeat.onlyIf(repeatContext -> true)
//                        .exponentialBackoff(Duration.ofSeconds(5), Duration.ofSeconds(10))
//                        .timeout(Duration.ofSeconds(30)))
//                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(5)))

//                .repeatWhen(longFlux -> Retry.fixedDelay(3, Duration.ofSeconds(5))).last();
    }

//    private Object reconnectFunction(ReconnectStrategy strategy) {
//
//    }


    @Override
    public Mono<CheckBars16Resp> checkExecute() {
        // TODO: заглушка
        return this.checkExecute(new CheckBarsData());
    }
}
