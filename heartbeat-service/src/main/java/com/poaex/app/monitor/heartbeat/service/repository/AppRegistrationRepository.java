package com.poaex.app.monitor.heartbeat.service.repository;

import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.entity.HeartbeatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRegistrationRepository extends MongoRepository<AppRegistration, String> {
    Page<AppRegistration> findByInstanceId(String instanceId, Pageable pageable);

    Page<AppRegistration> findByStatus(String up, Pageable pageable);
}
