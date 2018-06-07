package com.poaex.app.monitor.heartbeat.svc.service;

import com.mongodb.MongoClientOptions;
import com.poaex.app.monitor.heartbeat.exception.service.ServiceException;
import com.poaex.app.monitor.heartbeat.exception.service.ServiceMonitoringException;
import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import com.poaex.app.monitor.heartbeat.svc.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.svc.entity.HeartbeatLog;
import com.poaex.app.monitor.heartbeat.svc.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.svc.repository.HeartbeatLogRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.poaex.app.monitor.heartbeat.svc.MonitorHeartbeatSvc.DB_PAGE_SIZE;
import static com.poaex.app.monitor.heartbeat.svc.MonitorHeartbeatSvc.FIRST_PAGE;

@Service
public class HeartbeatService {

    @Autowired
    private HeartbeatLogRepository heartbeatLogRepository;

    @Autowired
    private AppRegistrationRepository appRegistrationRepository;

    @Autowired
    private IdentityService identityService;

    public void recordHeartbeat(Heartbeat heartbeat) throws ServiceException {

        //Fetch Registration
        AppRegistration application = appRegistrationRepository.findByMonitoringProfileId(heartbeat.getMonitoringProfileId().toString());

        if (application == null || application.getMonitoringProfileId() == null) {
            throw new ServiceSecurityException("Profile has not been registered yet!!");
        }
        String monitoringRegistrationProfileId = application.getMonitoringProfileId();
        String instanceId = application.getInstanceId();

        if (instanceId == null || monitoringRegistrationProfileId == null) {
            throw new ServiceException("Internal Error !!! Missing Instance Id " + heartbeat);
        }

        //Match with Heartbeat
        if(!monitoringRegistrationProfileId.equals(heartbeat.getMonitoringProfileId().toString())) {
            throw new ServiceSecurityException("Bad heartbeat!! " + heartbeat);
        }


        recordHeartbeat(instanceId, heartbeat);
    }

    public void startMonitoring(MonitoringProfile pr) throws ServiceSecurityException {
        //De Weed Duplicate Profile Id
        if(isDuplicateProfileId(pr.getMonitoringProfileId())) {
            throw new ServiceSecurityException("Bad Monitoring Profile Id" + pr);
        }
        String instanceId = identityService.createInstanceId(pr);
        AppRegistration app = new AppRegistration();
        BeanUtils.copyProperties(pr, app);
        app.setMonitoringProfileId(pr.getMonitoringProfileId().toString());
        app.setInstanceId(instanceId);
        app.setRegistrationTime(Instant.now().toEpochMilli());

        registerApp(instanceId, app);
    }

    private boolean isDuplicateProfileId(UUID monitoringProfileId) {
        AppRegistration appRegistration = appRegistrationRepository.findByMonitoringProfileId(monitoringProfileId.toString());
        return appRegistration != null;
    }

    public void stopMonitoring(MonitoringProfile pr) throws Exception {
        String instanceId = identityService.createInstanceId(pr);
        AppRegistration app = new AppRegistration();
        BeanUtils.copyProperties(pr, app);
        app.setInstanceId(instanceId);
        app.setShutdownTime(Instant.now().toEpochMilli());

        registerApp(instanceId, app);
    }

    @Transactional
    private void registerApp(String instanceId, AppRegistration app) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "registrationTime"));
        Pageable pageable = new PageRequest(FIRST_PAGE, DB_PAGE_SIZE, sort);
        Page<AppRegistration> appRegistrationPage = appRegistrationRepository.findByInstanceId(instanceId, pageable);

        while(appRegistrationPage.hasContent()) {
            appRegistrationRepository.deleteAll(appRegistrationPage.getContent());
            appRegistrationPage = appRegistrationRepository.findByInstanceId(instanceId, appRegistrationPage.nextPageable());
        }

        appRegistrationRepository.save(app);
    }

    private void recordHeartbeat(String instanceId, Heartbeat pr) {
        HeartbeatLog heartbeatLog = new HeartbeatLog();
        heartbeatLog.setLastNotifiedTime(pr.getLastheartbeatTime());
        heartbeatLog.setInstanceId(instanceId);
        heartbeatLogRepository.save(heartbeatLog);
    }
}
