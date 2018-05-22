package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.service.repository.HeartbeatLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.poaex.app.monitor.heartbeat.service.HeartbeatService.DB_PAGE_SIZE;
import static com.poaex.app.monitor.heartbeat.service.HeartbeatService.FIRST_PAGE;
import static com.poaex.app.monitor.heartbeat.service.HeartbeatService.HB_RESPONSE_THRESHOLD_MILLIS;

@Service
@Slf4j
public class MonitoringService {

    @Autowired
    private AppRegistrationRepository appRegistrationRepository;

    @Autowired
    private HeartbeatLogRepository heartbeatLogRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60000)
    public void notifyNonRespondingApp() {
        notifyNonRespondingApp(Instant.now().toEpochMilli(), HB_RESPONSE_THRESHOLD_MILLIS);
    }

    public void notifyNonRespondingApp(Long currentTimeEpoch, Long responseThreshold) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "startTime"));
        Pageable pageable = new PageRequest(FIRST_PAGE, DB_PAGE_SIZE, sort);
        Page<AppRegistration> appRegistrationPage = appRegistrationRepository.findByStatus("UP", pageable);
        //log.info("Found {} applications registered for monitoring", appRegistrationPage.getTotalElements());

        while (appRegistrationPage.hasContent()) {
            List<AppRegistration> appsToBeNotified = appRegistrationPage.stream()
                    .filter(app -> isStaleHeartbeat(app, currentTimeEpoch, responseThreshold))
                    .collect(Collectors.toList());
            appsToBeNotified.forEach(app -> notificationService.sendNotification(app));
            log.info("Processed {} applications registered for monitoring, alerted {} applications",
                    appRegistrationPage.getNumberOfElements(),
                    appsToBeNotified.size());

            appRegistrationPage = appRegistrationRepository.findByStatus("UP", pageable.next());
        }

    }


    private boolean isStaleHeartbeat(AppRegistration appRegistration, Long currentTimeEpoch, Long responseThreshold) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "lastNotifiedTime"));
        Pageable pageable = new PageRequest(FIRST_PAGE, 1, sort);
        return heartbeatLogRepository.findByInstanceId(appRegistration.getInstanceId(), pageable)
                .stream()
                .findFirst()
                .filter(hb -> hb != null)
                .map(hb -> {
                    Long lastHbTime = Optional.ofNullable(hb.getLastNotifiedTime()).orElse(0L);
                    long delay = currentTimeEpoch.longValue() - lastHbTime.longValue();
                    return delay > responseThreshold.longValue();
                })
                .get();
    }
}
