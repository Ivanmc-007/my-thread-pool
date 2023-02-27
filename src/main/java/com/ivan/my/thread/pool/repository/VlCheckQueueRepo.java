package com.ivan.my.thread.pool.repository;

import com.ivan.my.thread.pool.model.entity.VlCheckQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VlCheckQueueRepo extends JpaRepository<VlCheckQueue, Long> {

    @Query(value = "SELECT vcq.id,\n" +
            "\tvcq.route_id,\n" +
            "\tvcq.stage_id,\n" +
            "\tvcq.check_id,\n" +
            "\tvcq.bid_id,\n" +
            "\tvcq.check_code,\n" +
            "\tvcq.check_start,\n" +
            "\tvcq.request_count\n" +
            "FROM public.vl_check_queue vcq \n" +
            "WHERE (:id IS NULL OR vcq.id=:id) AND \n" +
            "\t(:routeId IS NULL OR vcq.route_id=:routeId) AND \n" +
            "\t(:stageId IS NULL OR vcq.stage_id=:stageId) AND\n" +
            "\t(:checkId IS NULL OR vcq.check_id=:checkId) AND \n" +
            "\t(:bidId IS NULL OR vcq.bid_id=:bidId) AND \n" +
            "\t(:checkCode IS NULL OR vcq.check_code=:checkCode)", nativeQuery = true)
    List<VlCheckQueue> findWithFilter(Long id, Long routeId, Long stageId, Long checkId, Long bidId, String checkCode);

    @Query(value = "SELECT vcq.id,\n" +
            "\tvcq.route_id,\n" +
            "\tvcq.stage_id,\n" +
            "\tvcq.check_id,\n" +
            "\tvcq.bid_id,\n" +
            "\tvcq.check_code,\n" +
            "\tvcq.check_start,\n" +
            "\tvcq.request_count \n" +
            "FROM public.vl_check_queue vcq \n" +
            "WHERE check_start <= current_timestamp - interval '4 minute'", nativeQuery = true)
    List<VlCheckQueue> findWithInterval();

}
