package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.service.Application;
import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.entity.NotificationLog;
import com.poaex.app.monitor.heartbeat.service.repository.NotificationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Async
    public void sendNotification(AppRegistration appRegistration) {

        Page<NotificationLog> notifications = notificationLogRepository.findByInstanceIdAndIsNotified(appRegistration.getInstanceId(),
                true,
                new PageRequest(0, 1, Sort.Direction.DESC, "lastNotifiedTime"));


        if (!notifications.hasContent()
                || notifications.hasContent() &&
                (Instant.now().toEpochMilli() - notifications.getContent().get(0).getLastNotifiedTime())
                        > Application.NOTIFICATION_THROTTLE_LIMIT) {
            //Sample implementation - to be changed
            log.info("Sending notification for non responsive application " + appRegistration);
            NotificationLog n = new NotificationLog();
            n.setInstanceId(appRegistration.getInstanceId());
            n.setLastNotifiedTime(Instant.now().toEpochMilli());
            n.setRecepients(Arrays.asList("MonitoringOwner"));
            n.setNotified(true);
            notificationLogRepository.save(n);
        }

    }
}
