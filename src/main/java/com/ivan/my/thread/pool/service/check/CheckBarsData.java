package com.ivan.my.thread.pool.service.check;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckBarsData {

    private Long routeId;

    private Long stageId;

    private Long checkId;

    private Long bidId;

    private String checkCode;

    private LocalDateTime checkStart;

    private Integer requestCount;

}
