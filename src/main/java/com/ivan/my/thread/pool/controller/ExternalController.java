package com.ivan.my.thread.pool.controller;

import com.ivan.my.thread.pool.model.dto.response.RespDto;
import com.ivan.my.thread.pool.util.constant.RespStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ExternalController {

    int num = 0;

    int numBars17 = 0;

    private final List<String> checks = new ArrayList<String>() {
        {
            this.add("{\"creditRegisterId\":2352613668,\"clientUNN\":\"511111111\",\"dateCreateRequest\":\"30.03.2022\",\"status\":\"NOT ANSWER\"}");
            this.add("{\"creditRegisterId\":2352613668,\"clientUNN\":\"511111111\",\"dateCreateRequest\":\"30.03.2022\",\"status\":\"COMPLETE\",\"creditScore\":\"кредитная история положительная\",\"delayDayCount\":\"364\",\"delaySumCount\":\"5910.24\"}");
        }
    };

    private final List<String> checksBars17 = new ArrayList<String>() {
        {
            this.add("{\"creditRegisterId\" : null,\"clientUNN\" : null,\"dateCreateRequest\" : null,\"status\" : \"ER\",\"creditScore\" : null,\"delayDayCount\" : null,\"delaySumCount\" : null,\"result\" : \"Отсутствует дата согласия у клиента : = 86544\",\"value\" : \"False\"}");
            this.add("{\"creditRegisterId\":2356082379,\"clientUNN\":\"191069649\",\"dateCreateRequest\":\"24.10.2022\",\"status\":\"COMPLETE\",\"creditScore\":\"кредитная история положительная\",\"delayDayCount\":\"364\",\"delaySumCount\":\"5910.24\"}");
        }
    };

    @GetMapping("external/some-check")
    public ResponseEntity<String> getCheckResult(@RequestParam Long id) {
        System.out.println(Thread.currentThread().getName());
        num++;
        int index;
        if (num < 4) {
            index = 0;

        } else  {
            index = 1;
            num = 0;
        }
        String checkAsAnswer = checks.get(index);
        return ResponseEntity.ok(checkAsAnswer);
    }

    @GetMapping("external/some-check/bars-17")
    public ResponseEntity<String> getCheckResultBars17(@RequestParam Long id) {
        System.out.println(Thread.currentThread().getName());
        numBars17++;
        int index;
        if (numBars17 < 4) {
            index = 0;

        } else  {
            index = 1;
            numBars17 = 0;
        }
        String checkAsAnswer = checksBars17.get(index);
        return ResponseEntity.ok(checkAsAnswer);
    }


}
