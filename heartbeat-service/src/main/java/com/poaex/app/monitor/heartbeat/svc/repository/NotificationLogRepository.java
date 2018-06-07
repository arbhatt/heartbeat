package com.poaex.app.monitor.heartbeat.svc.repository;

import com.poaex.app.monitor.heartbeat.svc.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    Page<NotificationLog> findByInstanceIdAndIsNotified(String instanceId, boolean isNotified, Pageable pageable);
}
