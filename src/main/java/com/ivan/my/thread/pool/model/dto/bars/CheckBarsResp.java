package com.ivan.my.thread.pool.model.dto.bars;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckBarsResp {

    private String creditRegisterId;
    private String clientUNN;
    private String dateCreateRequest;
    private String status;
    private String creditScore;
    private String delayDayCount;
    private String delaySumCount;

}
