package com.ivan.my.thread.pool.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vl_check_queue", schema = "public")
public class VlCheckQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "check_id")
    private Long checkId;

    @Column(name = "bid_id")
    private Long bidId;

    @Column(name = "check_code")
    private String checkCode;

    @Column(name = "check_start")
    private LocalDateTime checkStart;

    @Column(name = "request_count")
    private Integer requestCount;

}
