package com.poaex.app.monitor.heartbeat.svc.repository;

import com.poaex.app.monitor.heartbeat.svc.entity.AppRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppRegistrationRepository extends MongoRepository<AppRegistration, String> {
    Page<AppRegistration> findByInstanceId(String instanceId, Pageable pageable);

    Page<AppRegistration> findByStatus(String up, Pageable pageable);

    AppRegistration findByMonitoringProfileId(String s);
}
