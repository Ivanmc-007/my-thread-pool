package com.ivan.my.thread.pool.service.check;

import com.ivan.my.thread.pool.repository.CheckRepo;
import com.ivan.my.thread.pool.util.constant.WebClientName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


interface TempCheck {

    void checkExecute();

}

class TempCheck16 implements TempCheck {

    private final WebClient webClient;

    private final Object params;

    public TempCheck16(Object params, WebClient webClient) {
        this.params = params;
        this.webClient = webClient;
    }

    private Mono<?> checkExecute(Object params) {
        return Mono.empty();
    }

    @Override
    public void checkExecute() {
        this.checkExecute(this.params).subscribe(o -> {
            // some logic
        });
    }

}

class TempCheck17 implements TempCheck {

    private final WebClient webClient;

    private final Object params;

    public TempCheck17(Object params, WebClient webClient) {
        this.params = params;
        this.webClient = webClient;
    }

    private Mono<?> checkExecute(Object params) {
        return Mono.empty();
    }

    @Override
    public void checkExecute() {
        this.checkExecute(this.params).subscribe(o -> {
            // some logic
        });
    }

}

class CheckExecutor {

    private final List<TempCheck> checks = new ArrayList<>();

    public void addCheck(TempCheck check) {
        this.checks.add(check);
    }

    public void execute() {
        checks.forEach(TempCheck::checkExecute);
    }

}


@Service
@RequiredArgsConstructor
public class CheckService {

    private final CheckRepo checkRepo;

    @Qualifier(WebClientName.LOCAL_WEB_CLIENT)
    private final WebClient localWebClient;


    public void initChecks(Long routeId, Long stageId, Long bidId) {
        // 1) где-то на интерфейсе их добавили (со статусом 'init')
        // 2) теперь извлекаем
        List<?> checkDB = checkRepo.findAllChecks(routeId, stageId, bidId);

        List<TempCheck> checks = new ArrayList<>();
        for(Object o: checkDB) {
//            if(o.getCode().equals("BARS_16")) {
//                // сходить в БД за данными
//                // создать объект BARS_16
//                // добавить в список на выполнение
//            }
//            if(o.getCode().equals("BARS_17")) {
//                // сходить в БД за данными
//                // создать объект BARS_17
//                // добавить в список на выполнение
//            }
            checks.add(new TempCheck17(null, localWebClient));
            checks.add(new TempCheck16(null, localWebClient));
            // add the last of as check
        }
        checks.forEach(tempCheck -> {
            System.out.println("*");
            tempCheck.checkExecute();
        });

    }

}
