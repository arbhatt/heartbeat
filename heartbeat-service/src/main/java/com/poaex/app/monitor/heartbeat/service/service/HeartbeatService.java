package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.entity.HeartbeatLog;
import com.poaex.app.monitor.heartbeat.service.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.service.repository.HeartbeatLogRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.poaex.app.monitor.heartbeat.service.HeartbeatService.DB_PAGE_SIZE;
import static com.poaex.app.monitor.heartbeat.service.HeartbeatService.FIRST_PAGE;

@Service
public class HeartbeatService {

    @Autowired
    private HeartbeatLogRepository heartbeatLogRepository;

    @Autowired
    private AppRegistrationRepository appRegistrationRepository;

    @Autowired
    private IdentityService identityService;

    public void recordHeartbeat(Heartbeat pr) throws ServiceSecurityException {

        //Determine Instance Id
        String instanceId = identityService.createInstanceId(pr);

        //Make sure app is registered
        AppRegistration appRegistration = appRegistrationRepository.findByInstanceId(instanceId,
                new PageRequest(FIRST_PAGE, 1))
                .stream()
                .findFirst()
                .orElseThrow(() ->  new ServiceSecurityException("Process not registered"));

        recordHeartbeat(instanceId, pr);
    }

    public void startMonitoring(Heartbeat pr) throws ServiceSecurityException {
        String instanceId = identityService.createInstanceId(pr);
        registerApp(instanceId, pr.start());
        recordHeartbeat(instanceId, pr  );
    }

    public void stopMonitoring(Heartbeat pr) throws Exception {
        String instanceId = identityService.createInstanceId(pr);
        registerApp(instanceId, pr.stop());
    }

    @Transactional
    private void registerApp(String instanceId, Heartbeat pr) {
        AppRegistration app = new AppRegistration();
        BeanUtils.copyProperties(pr, app);
        app.setInstanceId(instanceId);

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "startTime"));
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
