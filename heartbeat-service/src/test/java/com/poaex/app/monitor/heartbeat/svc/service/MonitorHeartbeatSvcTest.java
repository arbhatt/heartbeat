package com.poaex.app.monitor.heartbeat.svc.service;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import com.poaex.app.monitor.heartbeat.svc.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.svc.entity.HeartbeatLog;
import com.poaex.app.monitor.heartbeat.svc.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.svc.repository.HeartbeatLogRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitorHeartbeatSvcTest {

    @Mock
    private HeartbeatLogRepository heartbeatLogRepository;

    @Mock
    private AppRegistrationRepository appRegistrationRepository;

    @Mock
    private Page<AppRegistration> appRegistrationPage ;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private final HeartbeatService hbs = new HeartbeatService();

    private UUID uuid;

    @Before
    public void populateUuid() {
        uuid = UUID.randomUUID();
    }

    @Test
    public void startMonitoring() throws ServiceSecurityException {
        MonitoringProfile mp = stubMonitoringProfile();

        mockForRegistration();
        hbs.startMonitoring(mp);

        Mockito.verify(appRegistrationRepository).save(any(AppRegistration.class));

    }

    @Test
    public void stopMonitoring() throws Exception {
        MonitoringProfile mp = stubMonitoringProfile();

        mockForRegistration();
        hbs.stopMonitoring(mp);

        Mockito.verify(appRegistrationRepository).save(any(AppRegistration.class));
    }

    private void mockForRegistration() {
        when(appRegistrationPage.hasContent()).thenReturn(false);
        when(appRegistrationRepository.findByInstanceId(anyString(), any(PageRequest.class))).thenReturn(appRegistrationPage);
    }

    private MonitoringProfile stubMonitoringProfile() {
        MonitoringProfile mp = new MonitoringProfile();
        mp.setMonitoringProfileId(uuid);
        return mp ;
    }

    private Heartbeat stubHeartbeat() {
        Heartbeat pr = new Heartbeat();
        pr.setMonitoringProfileId(uuid);
        return pr;
    }
}