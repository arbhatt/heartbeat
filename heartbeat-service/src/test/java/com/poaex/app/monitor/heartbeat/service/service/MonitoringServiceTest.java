package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.service.entity.AppRegistration;
import com.poaex.app.monitor.heartbeat.service.entity.HeartbeatLog;
import com.poaex.app.monitor.heartbeat.service.repository.AppRegistrationRepository;
import com.poaex.app.monitor.heartbeat.service.repository.HeartbeatLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringServiceTest {

    @Mock
    private HeartbeatLogRepository heartbeatLogRepository;

    @Mock
    private AppRegistrationRepository appRegistrationRepository;

    @Mock
    private Page<AppRegistration> appRegistrationPage ;

    @Mock
    private IdentityService identityService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private final MonitoringService m = new MonitoringService();

    private static final Long now = Instant.now().toEpochMilli();
    private static final Long threshold = 500L;

    private Page<AppRegistration> appRegistrationPageWithNoValue() {
        return appRegistrationPage;
    }

    private ArgumentMatcher<PageRequest> secondPage() {
        return new ArgumentMatcher<PageRequest> (){

            @Override
            public boolean matches(Object o) {

                PageRequest p = (PageRequest)o;
                return p != null && p.getPageNumber() == 1L;
            }
        };
    }

    private ArgumentMatcher<PageRequest> firstPage() {
        return new ArgumentMatcher<PageRequest> (){

            @Override
            public boolean matches(Object o) {

                PageRequest p = (PageRequest)o;
                return p != null && p.getPageNumber() == 0L;
            }
        };
    }

    private Page<AppRegistration> appRegistrationPageWithValue() {
        AppRegistration a = new AppRegistration();
        a.setInstanceId("Id");
        return new PageImpl(Arrays.asList(a));
    }

    private HeartbeatLog staleHeartbeatLog() {
        HeartbeatLog h = new HeartbeatLog();
        h.setLastNotifiedTime(now - threshold - 10);
        return h;
    }

    private HeartbeatLog freshHeartbeatLog() {
        HeartbeatLog h = new HeartbeatLog();
        h.setLastNotifiedTime(now - threshold + 10);
        return h;
    }

    @Test
    public void notifyNonRespondingApp() {

        when(heartbeatLogRepository.findByInstanceId(anyString(), argThat(firstPage())))
                .thenReturn(new PageImpl<HeartbeatLog>(Arrays.asList(staleHeartbeatLog())));

        when(appRegistrationRepository.findByStatus(anyString(), argThat(firstPage())))
                .thenReturn(appRegistrationPageWithValue());

        when(appRegistrationRepository.findByStatus(anyString(), argThat(secondPage())))
                .thenReturn(appRegistrationPageWithNoValue());

        m.notifyNonRespondingApp(now,threshold);
        verify(notificationService, atLeastOnce()).sendNotification(any());

    }
    @Test
    public void notifyRespondingApp() {

        when(heartbeatLogRepository.findByInstanceId(anyString(), argThat(firstPage())))
                .thenReturn(new PageImpl<HeartbeatLog>(Arrays.asList(freshHeartbeatLog())));

        when(appRegistrationRepository.findByStatus(anyString(), argThat(firstPage())))
                .thenReturn(appRegistrationPageWithValue());

        when(appRegistrationRepository.findByStatus(anyString(), argThat(secondPage())))
                .thenReturn(appRegistrationPageWithNoValue());

        m.notifyNonRespondingApp(now,threshold);
        verify(notificationService, never()).sendNotification(any());

    }
}