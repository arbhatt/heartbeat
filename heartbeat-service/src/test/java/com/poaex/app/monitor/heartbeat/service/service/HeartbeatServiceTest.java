package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.entity.HeartbeatLog;
import com.poaex.app.monitor.heartbeat.service.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.service.repository.HeartbeatLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatServiceTest {

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

    @Test
    public void startMonitoring() throws ServiceSecurityException {
        Heartbeat pr = stubHeartbeat().start().beat();

        mockForRegistration();
        hbs.startMonitoring(pr);

        Mockito.verify(heartbeatLogRepository).save(any(HeartbeatLog.class));
        Mockito.verify(appRegistrationRepository).save(any(AppRegistration.class));

    }

    @Test
    public void stopMonitoring() throws Exception {
        Heartbeat pr = stubHeartbeat().start().beat();

        mockForRegistration();
        hbs.stopMonitoring(pr);

        Mockito.verify(heartbeatLogRepository, never()).save(any(HeartbeatLog.class));
        Mockito.verify(appRegistrationRepository).save(any(AppRegistration.class));
    }

    private void mockForRegistration() {
        when(appRegistrationPage.hasContent()).thenReturn(false);
        when(appRegistrationRepository.findByInstanceId(anyString(), any(PageRequest.class))).thenReturn(appRegistrationPage);
    }

    private Heartbeat stubHeartbeat() {
        Heartbeat pr = new Heartbeat();
        pr.setPid("T_0000");
        pr.setHostname("H_WHISPER");
        pr.setProcessSignature("P_ZOMBIE");


        pr.setEnvironment("TEST");

        return pr;
    }
}