package com.ivan.my.thread.pool.service.check;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckBars17Resp {

    private String creditRegisterId;

    private String clientUNN;

    private String dateCreateRequest;

    private String status;

    private String creditScore;

    private String delayDayCount;

    private String delaySumCount;

    private String result;

    private String value;

}
