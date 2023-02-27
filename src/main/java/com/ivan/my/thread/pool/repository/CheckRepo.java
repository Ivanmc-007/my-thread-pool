package com.ivan.my.thread.pool.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CheckRepo {

    public List<?> findAllChecks(Long routeId, Long stageId, Long bidId) {
        // TODO: заглушка
        return new ArrayList<>();
    }

}
