package com.poaex.app.monitor.heartbeat.svc.repository;

import com.poaex.app.monitor.heartbeat.svc.entity.HeartbeatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartbeatLogRepository extends MongoRepository<HeartbeatLog, String> {
    Page<HeartbeatLog> findByInstanceId(String instanceId, Pageable pageable);
}
